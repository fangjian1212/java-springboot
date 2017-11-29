/**
 * Created on 2006-09-20
 */
package com.fangjian.framework.utils.self;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("rawtypes")
public abstract class CollectionUtil {
    /**
     * Default constructor.
     *
     */
    private CollectionUtil() {
    }

    /**
     * 从all这个List集合中减去substract List集合中的元素.
     * @param all List
     * @param substract List
     * @return List
     */
    public static List substract(final List all, final List substract) {
        if (null == all) {
            return new ArrayList();
        }
        if (null == substract || substract.size() == 0) {
            return all;
        }
        List<Object> result = new ArrayList<Object>();
        Iterator it = all.iterator();
        while (it.hasNext()) {
            Object object = it.next();
            if (substract.contains(object)) {
                continue;
            } else {
                result.add(object);
            }
        }
        return result;
    }
}
