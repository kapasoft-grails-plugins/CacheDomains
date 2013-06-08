package com.minnehahalofts.app

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON
import javax.annotation.PostConstruct

class NodeDriverProxyService {

    def grailsApplication
    static transactional = false
    HTTPBuilder http

    @PostConstruct
    void init() {
        http = new HTTPBuilder('http://' + grailsApplication.config.cacheDomainsPlugin.host + ':' +
                grailsApplication.config.cacheDomainsPlugin.port)

    }

    public registerUpdate(Object domain){

        def path = '/' + grailsApplication.config.cacheDomainsPlugin.path + '/' +  grailsApplication.getDomainClass(domain.getClass().name)?.propertyName
        try{
            http.request(Method.POST, JSON){ req ->
                uri.path = path
                body = [
                        version: domain.version,
                        id: domain.id
                ]

                response.success = {resp, json ->
                    log.warn "cached object id: $domain.id and version: $domain.version; status code: " + resp.statusLine.statusCode
                }
            }
        }catch(Exception e){
            log.warn('Failure Initiate Connection with Node Driver: ' + e.message);
        }
    }
}
