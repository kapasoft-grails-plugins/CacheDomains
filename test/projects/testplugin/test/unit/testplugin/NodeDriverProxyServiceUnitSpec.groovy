package testplugin

import static org.junit.Assert.*

import grails.test.mixin.*
import org.junit.*
import grails.plugin.spock.UnitSpec
//import com.minnehahalofts.app.eventlisteners.CacheListener

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(com.minnehahalofts.app.NodeDriverProxyService)
class NodeDriverProxyServiceUnitSpec extends UnitSpec {

//    def nodeDriverProxyService

    def "sample test" (){
        setup:
//        com.minnehahalofts.app.eventlisteners.CacheListener temp = new com.minnehahalofts.app.eventlisteners.CacheListener()
//            com.minnehahalofts.app.NodeDriverProxyService service = new com.minnehahalofts.app.NodeDriverProxyService();
//        def service = nodeDriverProxyService
       def sercice = mainContext.getBean('nodeDriverProxyService')
        def test = 'some'

        when:
//        service.registerUpdate(1,2)
        test = 'changed'

        then:
        assert service
        assert 'changed' == test

    }
}


//class NodeDriverProxyServiceUnitSpec {
//
//    void setUp() {
//        // Setup logic here
//    }
//
//    void tearDown() {
//        // Tear down logic here
//    }
//
//    void testSomething() {
//        fail "Implement me"
//    }
//}
