package com.minnehahalofts.app.eventlisteners

import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEventListener
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
//import org.springframework.context.ApplicationEvent
import org.grails.datastore.mapping.engine.event.EventType
import static org.grails.datastore.mapping.engine.event.EventType.*
import org.springframework.context.ApplicationEvent
//import com.minnehahalofts.app.NodeDriverProxyService
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import com.minnehahalofts.app.NodeDriverProxyService



class CacheListener extends AbstractPersistenceEventListener{

    def nodeDriverProxyService

    public CacheListener(final Datastore datastore) {
        super (datastore)
    }

    @Override
    protected void onPersistenceEvent(final AbstractPersistenceEvent event) {
        switch(event.eventType) {
            case PreInsert:
//                println "PRE INSERT ${event.entityObject}"
                break
            case PostInsert:
                println "POST INSERT ${event.entityObject}"
                if(event.entityObject?.isCached){
                    nodeDriverProxyService?.registerUpdate(event.entityObject.id, event.entityObject.version)
                }
                break
            case PreUpdate:
//                println "PRE UPDATE ${event.entityObject}"
                break;
            case PostUpdate:
//                println "POST UPDATE ${event.entityObject}"
                if(event.entityObject?.isCached){
                    nodeDriverProxyService?.registerUpdate(event.entityObject.id, event.entityObject.version)
                }
                break;
            case PreDelete:
//                println "PRE DELETE ${event.entityObject}"
                break;
            case PostDelete:
//                println "POST DELETE ${event.entityObject}"
                break;
            case PreLoad:
//                println "PRE LOAD ${event.entityObject}"
                break;
            case PostLoad:
//                println "POST LOAD ${event.entityObject}"
                break;
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return true
    }

}