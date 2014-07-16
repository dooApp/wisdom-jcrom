package org.wisdom.jcrom.conf;

import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.configuration.Configuration;

import java.io.File;
import java.util.*;

/**
 * Created by antoine on 14/07/2014.
 */
public class WJcromConf {

    public static final String JCROM_PREFIX = "jcrom";
    public static final String JCROM_PACKAGE = "package";

    private final String alias;
    private final String nameSpace;
    private final String url;


    public WJcromConf(String alias, String url, String nameSpace) {
        this.alias = alias;
        this.nameSpace = nameSpace;
        this.url = url;
    }

    private WJcromConf(String alias, String url, Configuration config) {
        this(
                alias,
                url,
                config.getOrDie(JCROM_PACKAGE)
        );
    }

    public String getAlias() {
        return alias;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getUrl() {
        return url;
    }

    public Dictionary<String, String> toDico() {
        Dictionary<String, String> dico = new Hashtable<String, String>(3);
        dico.put("name", alias);
        dico.put("package", nameSpace);
        return dico;
    }

    /**
     * Extract all WJcromConf configuration from the parent configuration.
     * If the configuration is
     * jcrom.default.url = "plocal:/home/wisdom/db"
     * jcrom.test.url = "plocal:/home/wisdom/test/db"
     * <p/>
     * the sub configuration will be:
     * <p/>
     * [alias:default]
     * url = "plocal:/home/wisdom/db"
     * [alias:test]
     * url = "plocal:/home/wisdom/test/db"
     *
     * @param config
     * @return
     */
    public static Collection<WJcromConf> createFromApplicationConf(ApplicationConfiguration config) {
        Configuration jcrom = config.getConfiguration(JCROM_PREFIX);

        if (jcrom == null) {
            return Collections.EMPTY_SET;
        }

        Set<String> subkeys = new HashSet<String>();

        for (String key : jcrom.asProperties().stringPropertyNames()) {
            subkeys.add(key.split("\\.", 2)[0]);
        }

        Collection<WJcromConf> subconfs = new ArrayList<WJcromConf>(subkeys.size());

        for (String subkey : subkeys) {
            subconfs.add(new WJcromConf(subkey,
                    new File(new File(config.getBaseDir(), "conf"), "modeshape-" + subkey + ".json").toURI().toString(),
                    jcrom.getConfiguration(subkey)));
        }

        return subconfs;
    }

}
