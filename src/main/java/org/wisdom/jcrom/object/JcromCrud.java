package org.wisdom.jcrom.object;

import org.wisdom.api.model.Crud;

import java.io.Serializable;

/**
 * Created by antoine on 14/07/2014.
 */
public interface JcromCrud<T, I extends Serializable> extends Crud<T, I> {
}
