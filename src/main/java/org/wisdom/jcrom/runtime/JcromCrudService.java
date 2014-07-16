package org.wisdom.jcrom.runtime;

import org.jcrom.dao.AbstractJcrDAO;
import org.wisdom.api.model.EntityFilter;
import org.wisdom.api.model.Repository;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.concurrent.Callable;
import org.wisdom.jcrom.object.JcromCrud;

/**
 * Created by antoine on 14/07/2014.
 */
public class JcromCrudService<T> implements JcromCrud<T, String> {

    private final JcrRepository repository;

    private final Class<T> entityClass;

    private final AbstractJcrDAO<T> dao;

    private final String rootPath;

    /**
     * Flag used in order to know if the instance is used during a transaction in the current thread.
     */
    private static final ThreadLocal<Boolean> transaction = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    protected JcromCrudService(JcrRepository repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
        rootPath = entityClass.getSimpleName();
        dao = new AbstractJcrDAO<T>(repository.getSession(), repository.getJcrom()) {
        };
        try {
            Node parent = repository.getSession().getRootNode().getNode(rootPath);
            if (parent == null) {
                repository.getSession().getRootNode().addNode(rootPath);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        transaction.set(false);
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public Class<String> getIdClass() {
        return String.class;
    }

    @Override
    public T delete(T t) {
        return null;
    }

    @Override
    public void delete(String s) {

    }

    @Override
    public Iterable<T> delete(Iterable<T> ts) {
        return null;
    }

    @Override
    public T save(T t) {
        return dao.create(t);
    }

    @Override
    public Iterable<T> save(Iterable<T> ts) {
        return null;
    }

    @Override
    public T findOne(String s) {
        return dao.get(s);
    }

    @Override
    public T findOne(EntityFilter<T> tEntityFilter) {
        return null;
    }

    @Override
    public boolean exists(String s) {
        return dao.exists(s);
    }

    @Override
    public Iterable<T> findAll() {
        return dao.findAll(entityClass.getSimpleName());
    }

    @Override
    public Iterable<T> findAll(Iterable<String> strings) {
        return null;
    }

    @Override
    public Iterable<T> findAll(EntityFilter<T> tEntityFilter) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public void executeTransactionalBlock(Runnable runnable) {

    }

    @Override
    public <A> A executeTransactionalBlock(Callable<A> aCallable) {
        return null;
    }
}
