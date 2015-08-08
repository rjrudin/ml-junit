package com.rjrudin.marklogic.junit.spring;

import java.io.File;
import java.util.Set;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class ModulesLoadedEvent extends ApplicationEvent {

    public ModulesLoadedEvent(Set<File> loadedModules) {
        super(loadedModules);
    }

    @SuppressWarnings("unchecked")
    public Set<File> getLoadedModules() {
        return (Set<File>) this.getSource();
    }
}
