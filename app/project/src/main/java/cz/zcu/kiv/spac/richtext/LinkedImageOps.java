/*
 * Created 2014 by Tomas Mikula.
 *
 * The author dedicates this file to the public domain.
 */

package cz.zcu.kiv.spac.richtext;

import org.fxmisc.richtext.model.NodeSegmentOpsBase;


public class LinkedImageOps<S> extends NodeSegmentOpsBase<LinkedImage, S> {

    public LinkedImageOps() {
        super(new EmptyLinkedImage());
    }

    @Override
    public int length(LinkedImage linkedImage) {
        return linkedImage.isReal() ? 1 : 0;
    }

}
