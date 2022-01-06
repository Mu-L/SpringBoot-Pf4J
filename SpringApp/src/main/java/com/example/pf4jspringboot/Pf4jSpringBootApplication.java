package com.example.pf4jspringboot;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.pf4j.demo.api.Greeting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Pf4jSpringBootApplication {
    public static final String PLUGIN_PATH = "C:\\code\\AdvanceProductory\\plugins";
    public static final PluginManager pluginManager = new DefaultPluginManager(Paths.get(PLUGIN_PATH));


    public static void main(String[] args) {
        SpringApplication.run(Pf4jSpringBootApplication.class, args);
        initPluginManager();
    }

    private static void initPluginManager() {
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        pluginManager.disablePlugin("ImplPlanB");
        // retrieves the extensions for Greeting extension point
        List<Greeting> greetings = pluginManager.getExtensions(Greeting.class);
        System.out.println(String.format("Found %d extensions for extension point '%s'", greetings.size(), Greeting.class.getName()));
        for (Greeting greeting : greetings) {
            System.out.println(">>> " + greeting.getGreeting());
        }

        // print extensions from classpath (non plugin)
        System.out.println("Extensions added by classpath:");
        Set<String> extensionClassNames = pluginManager.getExtensionClassNames(null);
        for (String extension : extensionClassNames) {
            System.out.println("   " + extension);
        }

        System.out.println("Extension classes by classpath:");
        List<Class<? extends Greeting>> greetingsClasses = pluginManager.getExtensionClasses(Greeting.class);
        for (Class<? extends Greeting> greeting : greetingsClasses) {
            System.out.println("   Class: " + greeting.getCanonicalName());
        }

        // print extensions ids for each started plugin
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            System.out.println(String.format("Extensions added by plugin '%s':", pluginId));
            extensionClassNames = pluginManager.getExtensionClassNames(pluginId);
            for (String extension : extensionClassNames) {
                System.out.println("   " + extension);
            }
        }

        // print the extensions instances for Greeting extension point for each started plugin
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            System.out.println(String.format("Extensions instances added by plugin '%s' for extension point '%s':", pluginId, Greeting.class.getName()));
            List<Greeting> extensions = pluginManager.getExtensions(Greeting.class, pluginId);
            for (Object extension : extensions) {
                System.out.println("   " + extension);
            }
        }

        // print extensions instances from classpath (non plugin)
        System.out.println("Extensions instances added by classpath:");
        List extensions = pluginManager.getExtensions((String) null);
        for (Object extension : extensions) {
            System.out.println("   " + extension);
        }

        // print extensions instances for each started plugin
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            System.out.println(String.format("Extensions instances added by plugin '%s':", pluginId));
            extensions = pluginManager.getExtensions(pluginId);
            for (Object extension : extensions) {
                System.out.println("   " + extension);
            }
        }
    }


}
