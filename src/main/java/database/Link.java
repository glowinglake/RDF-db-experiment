package database;

import parser.ConstraintCompiler;
import parser.ConstraintCompiler.LinkageEnum;

/**
 * Created by ywang2 on 11/23/16.
 */
public class Link {
    Long sub;
    Long pred;
    Long obj;

    public Link() {
        sub = 0L;
        pred = 0L;
        obj = 0L;
    }

    public void SetLinkage(LinkageEnum e, Long v) {
        if (e == LinkageEnum.Subject) {
            sub = v;
        } else if (e == LinkageEnum.Object) {
            obj = v;
        } else if (e == LinkageEnum.Predicate) {
            pred = v;
        }
    }
}
