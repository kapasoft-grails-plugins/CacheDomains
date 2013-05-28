package com.minnehahalofts.app

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

class NodeDriverProxyService {

    def grailsApplication
    static transactional = false
    def http

    public registerUpdate(Long id, Long version){


        http = new HTTPBuilder('http://' + grailsApplication.config.cacheDomainsPlugin.host + ':' +
                grailsApplication.config.cacheDomainsPlugin.port + '/' + grailsApplication.config.cacheDomainsPlugin.api)
        println('***http://' + grailsApplication.config.cacheDomainsPlugin.host + ':' +
                grailsApplication.config.cacheDomainsPlugin.port + '/' + grailsApplication.config.cacheDomainsPlugin.api)
        try{
            http.request(Method.POST, JSON){ req ->
                body = [
                        version: version,
                        id: id
                ]

                response.success = {resp, json ->
                    log.warn "cached object id: $id and version: $version; status code: " + resp.statusLine.statusCode
                }
            }
        }catch(Exception e){
            log.warn('Failure Initiate Connection with Node Driver: ' + e.message);
        }
    }
}
