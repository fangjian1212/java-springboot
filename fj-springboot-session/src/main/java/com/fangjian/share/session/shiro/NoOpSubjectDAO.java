/*
 */
package com.fangjian.share.session.shiro;

import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.subject.Subject;

/**
 */
public class NoOpSubjectDAO implements SubjectDAO {
    @Override
    public Subject save(Subject subject) {
        return null;
    }

    @Override
    public void delete(Subject subject) {

    }
}
