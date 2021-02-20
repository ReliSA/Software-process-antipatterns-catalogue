/*
 * Created 2014 by Tomas Mikula.
 *
 * The author dedicates this file to the public domain.
 */

package cz.zcu.kiv.spac.richtext;

import javafx.scene.Node;

public class EmptyLinkedImage implements LinkedImage {

    @Override
    public boolean isReal() {
        return false;
    }

    @Override
    public String getImagePath() {
        return "";
    }

    @Override
    public Node createNode() {
        throw new AssertionError("Unreachable code");
    }
}
