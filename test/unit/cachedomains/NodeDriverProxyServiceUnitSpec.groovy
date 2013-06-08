package cachedomains

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import grails.plugin.spock.UnitSpec
import groovyx.net.http.HTTPBuilder
import groovy.mock.interceptor.MockFor
import groovyx.net.http.Method
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder.RequestConfigDelegate
import org.apache.commons.logging.impl.SLF4JLog
import spock.lang.Shared
import org.codehaus.groovy.grails.commons.ArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication

@TestMixin(GrailsUnitTestMixin)
@TestFor(com.minnehahalofts.app.NodeDriverProxyService)
class NodeDriverProxyServiceUnitSpec extends UnitSpec {
    @Shared def id = 2,
                ver = 3,
                status_code = 200,
                msg = 'test message',
                host = 'localhost',
                port = '3001',
                relative_path="api",
                domain = [
                        id:id,
                        version: ver,
                        clazz: {'Domain'}
                ]

    def "ensure params passed in correctly"(){
        setup:
        def httpBuildMock = new MockFor(HTTPBuilder)

        def reqPar = []
        def success

        def requestDelegate = [
                response: [:]
        ]

        httpBuildMock.demand.request{
            Method met, ContentType type, Closure b ->
            b.delegate = requestDelegate
            b.call()
            reqPar << [method: met, type: type, id: b.body.id, ver: b.body.version, path: b.uri.path]
            println 'got here'
        }

        when:
        httpBuildMock.use{
            service.grailsApplication.config.cacheDomainsPlugin.port = port
            service.grailsApplication.config.cacheDomainsPlugin.host = host
            service.grailsApplication.config.cacheDomainsPlugin.path = relative_path
            //@ToDo figure how to mock the grailsApplication.getDomainClass(domain.getClass().name)?.clazz
            service.http = new HTTPBuilder('defaultHost:port')
            service.registerUpdate(domain)
        }

        then:
        assert reqPar[0].method == Method.POST
        assert reqPar[0].type == ContentType.JSON
        assert reqPar.ver[0] == ver
        assert reqPar.id[0] == id
        assert reqPar[0].path == '/' + relative_path + '/null'
    }

    def "ensure response is handled accordingly for success and failure states"(){

        setup:
        def logTo = []
        def success
        def httpBuildMock = new MockFor(HTTPBuilder.class)

        def logService = [
                warn: {String message ->
                    logTo << [warn: message]
                }
        ] as org.apache.commons.logging.Log

        def requestDelegate = [
                response: [
                        'statusLine': [
                                'protocol': 'HTTP/1.1','statusCode': status_code, 'status': 'OK'
                        ]
                ],
        ]

        httpBuildMock.demand.request(1){
            Method met, ContentType type, Closure b ->
            b.delegate = requestDelegate
            b.call()
            if(success) {
                requestDelegate.response.success(requestDelegate.response,[:])
            }else{
                throw new Exception(msg)
            }
        }
        service.log = logService

        httpBuildMock.use{
            service.grailsApplication.config.cacheDomainsPlugin.port = port
            service.grailsApplication.config.cacheDomainsPlugin.host = host
            service.grailsApplication.config.cacheDomainsPlugin.path = relative_path
            success = state
            service.http = new HTTPBuilder('defaultHost:port')
            service.registerUpdate(domain)
        }

        expect:
        assert logTo[0].warn == message

        where:
        state   | message
        false   | 'Failure Initiate Connection with Node Driver: ' + msg
        true    | 'cached object id: ' + domain.id + ' and version: ' + domain.version + '; status code: ' + status_code
    }

}
