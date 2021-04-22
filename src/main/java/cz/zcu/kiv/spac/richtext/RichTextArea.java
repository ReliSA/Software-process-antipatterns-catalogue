package cz.zcu.kiv.spac.richtext;

import static org.fxmisc.richtext.model.TwoDimensional.Bias.Backward;
import static org.fxmisc.richtext.model.TwoDimensional.Bias.Forward;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.collection.iteration.ReversiblePeekingIterable;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import cz.zcu.kiv.spac.data.Constants;
import cz.zcu.kiv.spac.html.HTMLGenerator;
import cz.zcu.kiv.spac.markdown.MarkdownGenerator;
import cz.zcu.kiv.spac.markdown.MarkdownParser;
import javafx.scene.control.*;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledSegment;
import org.fxmisc.richtext.model.TextOps;
import org.reactfx.SuspendableNo;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.reactfx.util.Either;

/**
 * Class representing rich textarea.
 * It must extends VBox, or it cannot be used as a element in javafx pane.
 * Reused from https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/richtext/RichTextDemo.java.
 */
public class RichTextArea extends VBox {

    private final TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
    private final LinkedImageOps<TextStyle> linkedImageOps = new LinkedImageOps<>();
    private Map<Either, String> imagesPath = new HashMap<>();

    private Stage mainStage;

    private final SuspendableNo updatingToolbar = new SuspendableNo();

    private final GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle> area =
            new GenericStyledArea<>(
                    ParStyle.EMPTY,                                                 // default paragraph style
                    (txtFlow,pstyle) -> txtFlow.setStyle(pstyle.toCss()),        	// paragraph style setter

                    TextStyle.EMPTY.updateFontSize(12).updateFontFamily("Serif").updateTextColor(Color.BLACK),  // default segment style
                    styledTextOps._or(linkedImageOps, (s1, s2) -> Optional.empty()),                            // segment operations
                    seg -> createNode(seg, (text, style) -> text.setStyle(style.toCss())));                   // Node creator and segment style setter
    {
        area.setWrapText(true);
        area.setStyleCodecs
                (
                        ParStyle.CODEC, Codec.styledSegmentCodec
                                (
                                        Codec.eitherCodec(Codec.STRING_CODEC, LinkedImage.codec()), TextStyle.CODEC
                                )
                );
        area.setParagraphGraphicFactory( new BulletFactory( area ) );
    }

    public RichTextArea(Stage primaryStage) {

        mainStage = primaryStage;

        Button boldBtn = createButton("bold", this::toggleBold, "Bold");
        Button italicBtn = createButton("italic", this::toggleItalic, "Italic");
        Button underlineBtn = createButton("underline", this::toggleUnderline, "Underline");
        Button insertImageBtn
                = createButton("insertimage", this::createImage, "Insert Image");
        Button increaseIndentBtn = createButton("increaseIndent", this::increaseIndent, "Increase indent");
        Button decreaseIndentBtn = createButton("decreaseIndent", this::decreaseIndent, "Decrease indent");

        area.beingUpdatedProperty().addListener((o, old, beingUpdated) -> {

            if(!beingUpdated) {

                boolean bold, italic, underline;

                IndexRange selection = area.getSelection();
                if(selection.getLength() != 0) {

                    StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
                    bold = styles.styleStream().anyMatch(s -> s.bold.orElse(false));
                    italic = styles.styleStream().anyMatch(s -> s.italic.orElse(false));
                    underline = styles.styleStream().anyMatch(s -> s.underline.orElse(false));

                } else {

                    int p = area.getCurrentParagraph();
                    int col = area.getCaretColumn();
                    TextStyle style = area.getStyleAtPosition(p, col);
                    bold = style.bold.orElse(false);
                    italic = style.italic.orElse(false);
                    underline = style.underline.orElse(false);
                }

                updatingToolbar.suspendWhile(() -> {

                    if(bold) {

                        if(!boldBtn.getStyleClass().contains("pressed")) {

                            boldBtn.getStyleClass().add("pressed");
                        }

                    } else {

                        boldBtn.getStyleClass().remove("pressed");
                    }

                    if(italic) {

                        if(!italicBtn.getStyleClass().contains("pressed")) {

                            italicBtn.getStyleClass().add("pressed");
                        }

                    } else {

                        italicBtn.getStyleClass().remove("pressed");
                    }

                    if(underline) {

                        if(!underlineBtn.getStyleClass().contains("pressed")) {

                            underlineBtn.getStyleClass().add("pressed");
                        }

                    } else {

                        underlineBtn.getStyleClass().remove("pressed");
                    }
                });
            }
        });

        /**
        ToolBar toolBar1 = new ToolBar(
                boldBtn, italicBtn, underlineBtn, new Separator(Orientation.VERTICAL),
                increaseIndentBtn, decreaseIndentBtn, new Separator(Orientation.VERTICAL),
                insertImageBtn);
         **/

        ToolBar toolBar1 = new ToolBar(
                boldBtn, italicBtn, underlineBtn, new Separator(Orientation.VERTICAL),
                increaseIndentBtn, decreaseIndentBtn, new Separator(Orientation.VERTICAL));

        VirtualizedScrollPane<GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle>> vsPane = new VirtualizedScrollPane<>(area);

        VBox.setVgrow(vsPane, Priority.ALWAYS);
        this.getChildren().addAll(toolBar1, vsPane);
    }

    private Node createNode(StyledSegment<Either<String, LinkedImage>, TextStyle> seg,
                            BiConsumer<? super TextExt, TextStyle> applyStyle) {

        return seg.getSegment().unify(
                text -> StyledTextArea.createStyledTextNode(text, seg.getStyle(), applyStyle),
                LinkedImage::createNode
        );
    }

    private Button createButton(String styleClass, Runnable action, String toolTip) {

        Button button = new Button();
        button.getStyleClass().add(styleClass);
        button.setOnAction(evt -> {
            action.run();
            area.requestFocus();
        });
        button.setPrefWidth(25);
        button.setPrefHeight(25);
        if (toolTip != null) {
            button.setTooltip(new Tooltip(toolTip));
        }
        return button;
    }

    private void toggleBold() {

        updateStyleInSelection(spans -> TextStyle.bold(!spans.styleStream().allMatch(style -> style.bold.orElse(false))));
    }

    private void toggleItalic() {

        updateStyleInSelection(spans -> TextStyle.italic(!spans.styleStream().allMatch(style -> style.italic.orElse(false))));
    }

    private void toggleUnderline() {

        updateStyleInSelection(spans -> TextStyle.underline(!spans.styleStream().allMatch(style -> style.underline.orElse(false))));
    }

    /**
     * Action listener which inserts a new image at the current caret position.
     */
    private void createImage() {

        String initialDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Insert image");
        fileChooser.setInitialDirectory(new File(initialDir));
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            String imagePath = selectedFile.getAbsolutePath();
            imagePath = imagePath.replace('\\',  '/');

            ReadOnlyStyledDocument ros = createImage(imagePath);
            area.replaceSelection(ros);
        }
    }

    /**
     * Create image object.
     * @param path - Path to image.
     * @return Object representing image.
     */
    private ReadOnlyStyledDocument createImage(String path) {

        // Create new image segment from image.
        Either segment = Either.right(new RealLinkedImage(path));

        // Create ReadOnlyStyledDocument for displaying it in rich textarea.
        // TODO: GenericEditableStyledDocument, ReadOnlyStyledDocument, SimpleEditableStyledDocument -> try all
        ReadOnlyStyledDocument<ParStyle, Either<String, LinkedImage>, TextStyle> ros =
                ReadOnlyStyledDocument.fromSegment(segment, ParStyle.EMPTY, TextStyle.EMPTY, area.getSegOps());

        // Also put this segment to map for extracting image path.
        // Maybe it can be done by parsing something, but i'm not sure it is possible (see parsing content).
        imagesPath.put(segment, path);

        return ros;
    }

    private void increaseIndent() {

        updateParagraphStyleInSelection(ParStyle::increaseIndent);
    }

    private void decreaseIndent() {

        updateParagraphStyleInSelection(ParStyle::decreaseIndent);
    }

    /**
     * Parse markdown text and extract all elements, then put them in area.
     * @param markdownText - Content in markdown.
     */
    public void setContent(String markdownText) {

        MutableDataHolder options = MarkdownParser.getDataOptions();

        Parser parser = Parser.builder(options).build();

        // You can re-use parser and renderer instances
        com.vladsch.flexmark.util.ast.Node document = parser.parse(markdownText);

        // If catalogue does not have any field, then return null.
        if (!document.hasChildren()) {

            this.area.appendText(markdownText);
            return;
        }

        String htmlText = HTMLGenerator.generateHTMLContentFromMarkdown(options, document);

        org.commonmark.parser.Parser commonParser = org.commonmark.parser.Parser.builder().build();
        org.commonmark.node.Node commonDocument = commonParser.parse(markdownText);

        parseRichTextContentNode(document.getChildren(), 0);
    }

    /**
     * Parse flexmark document and fill area with content.
     * @param childrens - Child elements.
     */
    private void parseRichTextContentNode(ReversiblePeekingIterable<com.vladsch.flexmark.util.ast.Node> childrens, int indent) {

        Iterator<com.vladsch.flexmark.util.ast.Node> it = childrens.iterator();

        // Iterate through every child.
        while (it.hasNext()) {

            com.vladsch.flexmark.util.ast.Node node = it.next();

            // BulletList.
            if (node.getClass() == BulletList.class) {

                BulletList list = (BulletList) node;

                if (list.getOpeningMarker() == '-') {

                    parseRichTextContentNode(node.getChildren(), indent + 1);

                } else {

                    parseRichTextContentNode(node.getChildren(), indent);
                }

                // BulletList item.
            } else if (node.getClass() == BulletListItem.class) {

                parseRichTextContentNode(node.getChildren(), indent);

                // Paragraph.
            } else if (node.getClass() == com.vladsch.flexmark.ast.Paragraph.class) {

                com.vladsch.flexmark.ast.Paragraph paragraph = (com.vladsch.flexmark.ast.Paragraph) node;

                Iterator<com.vladsch.flexmark.util.ast.Node> itData = paragraph.getChildIterator();

                boolean useUnderline = false;
                boolean nextUsed = false;

                com.vladsch.flexmark.util.ast.Node dataNode = null;

                while (itData.hasNext()) {

                    if (!nextUsed){

                        dataNode = itData.next();

                    } else {

                        nextUsed = false;
                    }

                    // Underline.
                    if (dataNode.getClass() == HtmlInline.class) {

                        useUnderline = !useUnderline;

                        // Bold text.
                    } else if (dataNode.getClass() == StrongEmphasis.class) {

                        appendStrongEmphasis(dataNode, useUnderline);

                        // Classic / italic text.
                    } else if (dataNode.getClass() == Emphasis.class) {

                        appendEmphasis(dataNode, useUnderline);

                        // Image.
                    } else if (dataNode.getClass() == Image.class) {

                        appendImage(dataNode);

                        // Classic text..
                    } else if (dataNode.getClass() == Text.class) {

                        appendText(dataNode, useUnderline, false, false);

                        // Line breaker.
                    } else if (dataNode.getClass() == SoftLineBreak.class) {

                        appendLineBreak(dataNode);
                        indent = 0;

                        // Link.
                    } else if (dataNode.getClass() == Link.class) {

                        appendLink(dataNode, useUnderline);

                    } else {

                        System.out.println("Unsupported class");
                        continue;
                    }

                    // Set indent.
                    ParStyle style = area.getContent().getParagraph(area.getCurrentParagraph()).getParagraphStyle();
                    style = style.updateIndent(new Indent(indent));
                    area.getContent().setParagraphStyle(area.getCurrentParagraph(), style);
                }
            }

            if (it.hasNext()) {

                area.appendText(Constants.LINE_BREAKER_CRLF);
            }
        }
    }

    /**
     * Append image to area.
     * @param dataNode - Node.
     * @param useUnderline - True if text must be underlined, false if not.
     */
    private void appendEmphasis(com.vladsch.flexmark.util.ast.Node dataNode, boolean useUnderline) {

        appendEmphasis(dataNode, useUnderline, false);
    }

    /**
     * Append image to area.
     * @param dataNode - Node.
     * @param useUnderline - True if text must be underlined, false if not.
     * @param useBold - True if text bust be bold, false if not.
     */
    private void appendEmphasis(com.vladsch.flexmark.util.ast.Node dataNode, boolean useUnderline, boolean useBold) {

        Emphasis emphasis = (Emphasis) dataNode;
        boolean useItalic = false;

        // Check if use italic.
        if ((emphasis.getOpeningMarker().toString().equals("*") && emphasis.getClosingMarker().toString().equals("*")) ||
                (emphasis.getOpeningMarker().toString().equals("_") && emphasis.getClosingMarker().toString().equals("_"))) {

            useItalic = true;
        }

        // Append elements.
        if (emphasis.hasChildren()) {

            Iterator<com.vladsch.flexmark.util.ast.Node> emphasisChildrensIt = emphasis.getChildIterator();

            boolean useUnderlineEmphasis = useUnderline;

            while (emphasisChildrensIt.hasNext()) {

                com.vladsch.flexmark.util.ast.Node emphasisDataNode = emphasisChildrensIt.next();

                // Strong emphasis.
                if (emphasisDataNode.getClass() == StrongEmphasis.class) {

                    appendStrongEmphasis(emphasisDataNode, useUnderlineEmphasis, useItalic);

                    // Link.
                } else if (emphasisDataNode.getClass() == Link.class) {

                    appendLink(emphasisDataNode, useUnderlineEmphasis, useItalic, useBold);

                    // Text.
                } else if (emphasisDataNode.getClass() == Text.class) {

                    appendText(emphasisDataNode, useUnderlineEmphasis, useItalic, useBold);

                    // Underline.
                } else if (emphasisDataNode.getClass() == HtmlInline.class) {

                    useUnderlineEmphasis = !useUnderlineEmphasis;

                } else {

                    System.out.println("Unsupported class in emphasis.");
                }
            }
        }
    }

    /**
     * Append image to area.
     * @param dataNode - Node.
     */
    private void appendImage(com.vladsch.flexmark.util.ast.Node dataNode) {

        // Image text + url
        Image image = (Image) dataNode;
        ReadOnlyStyledDocument ros = createImage(image.getUrl().toString());

        // TODO: nefunguje insert obrázků
        //area.append(ros);
        IndexRange range = area.getParagraphSelection(area.getCurrentParagraph() - 1);

        //area.replace(range.getStart(), range.getEnd(), ros);
    }

    /**
     * Append classic text to area.
     * @param dataNode - Node.
     * @param useUnderline - True if text must be underlined, false if not.
     * @param useItalic - True if use italic, false if not.
     * @param useBold - True if text bust be bold, false if not.
     */
    private void appendText(com.vladsch.flexmark.util.ast.Node dataNode, boolean useUnderline, boolean useItalic, boolean useBold) {

        TextStyle style = setDefaultTextStyles();

        if (useItalic) {

            style = style.updateItalic(true);
        }

        if (useUnderline) {

            style = style.updateUnderline(true);
        }

        if (useBold) {

            style = style.updateBold(true);
        }

        Text text = (Text) dataNode;
        Either segment = Either.left(text.getChars().toString());
        area.append(segment, style);
    }

    /**
     * Append line break to area.
     * @param dataNode - Node.
     */
    private void appendLineBreak(com.vladsch.flexmark.util.ast.Node dataNode) {

        SoftLineBreak softLineBreak = (SoftLineBreak) dataNode;
        area.appendText(softLineBreak.getChars().toString());
    }

    /**
     * Append strong emphasis to area.
     * @param dataNode - Node.
     * @param useUnderline - True if text must be underlined, false if not.
     */
    private void appendStrongEmphasis(com.vladsch.flexmark.util.ast.Node dataNode, boolean useUnderline) {

        appendStrongEmphasis(dataNode, useUnderline, false);
    }

    /**
     * Append strong emphasis to area.
     * @param dataNode - Node.
     * @param useUnderline - True if text must be underlined, false if not.
     * @param useItalic - True if use italic, false if not.
     */
    private void appendStrongEmphasis(com.vladsch.flexmark.util.ast.Node dataNode, boolean useUnderline, boolean useItalic) {

        StrongEmphasis strongEmphasis = (StrongEmphasis) dataNode;

        // Append elements.
        if (strongEmphasis.hasChildren()) {

            Iterator<com.vladsch.flexmark.util.ast.Node> emphasisChildrensIt = strongEmphasis.getChildIterator();

            boolean useUnderlineEmphasis = useUnderline;

            while (emphasisChildrensIt.hasNext()) {

                com.vladsch.flexmark.util.ast.Node emphasisDataNode = emphasisChildrensIt.next();

                // Strong emphasis.
                if (emphasisDataNode.getClass() == Emphasis.class) {

                    appendEmphasis(emphasisDataNode, useUnderlineEmphasis, true);

                    // Link.
                } else if (emphasisDataNode.getClass() == Link.class) {

                    appendLink(emphasisDataNode, useUnderlineEmphasis, useItalic, true);

                    // Text.
                } else if (emphasisDataNode.getClass() == Text.class) {

                    appendText(emphasisDataNode, useUnderlineEmphasis, useItalic, true);

                    // Underline.
                } else if (emphasisDataNode.getClass() == HtmlInline.class) {

                    useUnderlineEmphasis = !useUnderlineEmphasis;

                } else {

                    System.out.println("Unsupported class in Strong emphasis.");
                }
            }
        }
    }

    /**
     * Append link to area.
     * @param dataNode - Node.
     * @param useUnderline - True if text must be underlined, false if not.
     * @param useItalic - True if use italic, false if not.
     * @param useBold - True if text bust be bold, false if not.
     */
    private void appendLink(com.vladsch.flexmark.util.ast.Node dataNode, boolean useUnderline, boolean useItalic, boolean useBold) {

        TextStyle style = setDefaultTextStyles();

        if (useUnderline) {

            style = style.updateUnderline(true);
        }

        if (useItalic) {

            style = style.updateItalic(true);
        }

        if (useBold) {

            style = style.updateBold(true);
        }

        Link link = (Link) dataNode;
        Either segment = Either.left(link.getText().toString());
        area.append(segment, style);
    }

    /**
     * Append link to area.
     * @param dataNode - Node.
     * @param useUnderline - True if text must be underlined, false if not.
     */
    private void appendLink(com.vladsch.flexmark.util.ast.Node dataNode, boolean useUnderline) {

        appendLink(dataNode, useUnderline, false, false);
    }

    /**
     * Parse content from rich textarea in markdown format.
     * @return Content from rich textarea in markdown format.
     */
    public String getText() {

        StringBuilder sb = new StringBuilder();

        // Paragraph -> single line.
        // Segment -> Single text with properties or image.
        // Segment as text -> "temporary <b>text</b>" -> 2 segments, where first contains "temporary" and second contains
        // "text" with bold style. <b></b> is just for representing bold in this comment.
        Iterator<Paragraph<ParStyle, Either<String, LinkedImage>, TextStyle>> it = area.getContent().getParagraphs().iterator();

        boolean bulletListBefore = false;

        while (it.hasNext()) {

            Paragraph paragraph = it.next();

            // Get segments in paragraph and paragraph style.
            List segments = paragraph.getStyledSegments();
            ParStyle paragraphStyle = (ParStyle) paragraph.getParagraphStyle();

            // Get paragraph indent.
            Optional<Indent> indent = paragraphStyle.indent;

            // Get indent.
            int iIndent = indent.isEmpty() ? 0 : indent.get().level;

            // Apply indent.
            if (iIndent > 0) {

                sb.append(MarkdownGenerator.createLevelFromIndent(iIndent));

            } else {

                if (bulletListBefore) {

                    sb.append(Constants.LINE_BREAKER_CRLF);
                }
            }


            // Parse paragraph segments.
            for (Object oSegment : segments) {

                StyledSegment styledSegment = (StyledSegment) oSegment;
                TextStyle textStyle = (TextStyle) styledSegment.getStyle();

                // Bold / Italic / Underline properties.
                Optional<Boolean> bold = textStyle.bold;
                boolean isBold = false;

                if (bold.isPresent()) {

                    isBold = bold.get();
                }

                Optional<Boolean> italic = textStyle.italic;
                boolean isItalic = false;

                if (italic.isPresent()) {

                    isItalic = italic.get();
                }

                Optional<Boolean> underline = textStyle.underline;
                boolean isUnderline = false;

                if (underline.isPresent()) {

                    isUnderline = underline.get();
                }

                // Parse image if there is any.
                Either segment = (Either) styledSegment.getSegment();

                String text = "";

                boolean isImage = false;

                // Check if segment is in image map -> get full path to image.
                if (imagesPath.containsKey(segment)) {

                    text = imagesPath.get(segment);
                    isImage = true;

                } else {

                    // Otherwise get normal text from segment.
                    text = segment.getLeft().toString();
                }

                // Get markdown content.
                sb.append(MarkdownGenerator.convertSegmentText(text, isBold, isItalic, isUnderline, isImage));
            }

            // Add line breakers.
            if (it.hasNext()) {

                sb.append(Constants.LINE_BREAKER_CRLF);
            }

            if (iIndent > 0) {

                bulletListBefore = true;

            } else {

                bulletListBefore = false;
            }
        }

        return sb.toString();
    }

    private void updateStyleInSelection(Function<StyleSpans<TextStyle>, TextStyle> mixinGetter) {

        IndexRange selection = area.getSelection();
        if(selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            TextStyle mixin = mixinGetter.apply(styles);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            area.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private void updateParagraphStyleInSelection(Function<ParStyle, ParStyle> updater) {

        IndexRange selection = area.getSelection();
        int startPar = area.offsetToPosition(selection.getStart(), Forward).getMajor();
        int endPar = area.offsetToPosition(selection.getEnd(), Backward).getMajor();
        for(int i = startPar; i <= endPar; ++i) {
            Paragraph<ParStyle, Either<String, LinkedImage>, TextStyle> paragraph = area.getParagraph(i);
            area.setParagraphStyle(i, updater.apply(paragraph.getParagraphStyle()));
        }
    }

    /**
     * Prepare text style with predefined styles.
     * @return
     */
    private TextStyle setDefaultTextStyles() {

        return new TextStyle().updateFontSize(12).updateFontFamily("Serif").updateTextColor(Color.BLACK);
    }
}
