package cz.zcu.kiv.spac.data.antipattern;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import cz.zcu.kiv.spac.markdown.MarkdownFormatter;
import cz.zcu.kiv.spac.markdown.MarkdownParser;

import java.util.Arrays;

public class Antipattern {

    private String name;
    private String markdownContent;

    public Antipattern(String name, String markdownContent) {

        this.name = name;
        this.markdownContent = markdownContent;

        // TODO: Parse markdown content and extract all fields.
        MarkdownParser.parse();
    }

    private String getMarkdownContent() {

        return markdownContent;
    }

    public String getName() {

        return name;
    }

    public String generateHTMLContent() {

        String content = MarkdownFormatter.formatMarkdownTable(getMarkdownContent());

        MutableDataHolder options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create(),
                TaskListExtension.create()));

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        com.vladsch.flexmark.util.ast.Node document = parser.parse(content);
        return renderer.render(document);
    }

}
