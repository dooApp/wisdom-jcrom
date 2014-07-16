package org.wisdom.jcrom.runtime;

import org.jcrom.Jcrom;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.jcrom.object.JcromCrud;
import org.wisdom.api.model.Crud;
import org.wisdom.api.model.Repository;
import org.wisdom.jcrom.conf.WJcromConf;

import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import java.util.*;

/**
 * Created by antoine on 14/07/2014.
 */
public class JcrRepository implements Repository<javax.jcr.Repository> {

    private Logger logger = LoggerFactory.getLogger(JcromCrudProvider.class);

    private final javax.jcr.Repository repository;
    private final WJcromConf conf;
    private final Jcrom jcrom;
    private final Session session;

    private Collection<ServiceRegistration> registrations = new ArrayList<>();

    private Collection<JcromCrud<?, ?>> crudServices = new ArrayList<>();

    public JcrRepository(WJcromConf conf) throws RepositoryException {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("org.modeshape.jcr.URL", conf.getUrl());
        logger.info("Loading repository from " + conf.getUrl());
        javax.jcr.Repository loadedRepository = null;
        for (RepositoryFactory factory : ServiceLoader.load(RepositoryFactory.class)) {
            logger.info("Using " + factory);
            loadedRepository = factory.getRepository(parameters);
            if (loadedRepository != null) break;
        }
        this.repository = loadedRepository;
        this.jcrom = new Jcrom();
        this.conf = conf;
        this.session = repository.login();
    }

    public WJcromConf getConf() {
        return conf;
    }

    protected void addCrudService(Class entity) throws RepositoryException {
        jcrom.map(entity);
        crudServices.add(new JcromCrudService<Object>(this, entity));
    }

    protected void registerAllCrud(BundleContext context) {
        for (JcromCrud crud : crudServices) {
            Dictionary prop = conf.toDico();
            prop.put(Crud.ENTITY_CLASS_PROPERTY, crud.getEntityClass());
            prop.put(Crud.ENTITY_CLASSNAME_PROPERTY, crud.getEntityClass().getName());
            registrations.add(context.registerService(new String[]{Crud.class.getName(), JcromCrud.class.getName()}, crud, prop));
        }
    }

    protected void destroy() {
        for (ServiceRegistration reg : registrations) {
            reg.unregister();
        }

        registrations.clear();
        crudServices.clear();

        session.logout();
    }

    public javax.jcr.Repository getRepository() {
        return repository;
    }

    public Jcrom getJcrom() {
        return jcrom;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public Collection<Crud<?, ?>> getCrudServices() {
        return (Collection) crudServices;
    }


    @Override
    public String getName() {
        return repository.toString();
    }

    @Override
    public String getType() {
        return "jcr-repository";
    }

    @Override
    public Class<javax.jcr.Repository> getRepositoryClass() {
        return javax.jcr.Repository.class;
    }

    @Override
    public javax.jcr.Repository get() {
        return repository;
    }

}
