// Copyright 2011 Kiel Hodges
package replicant

import org.scalatest._
import org.scalatest.matchers._
import org.scalatest.junit.JUnitRunner
import org.junit._
import org.junit.runner._
import testing._

@RunWith(classOf[JUnitRunner])
class MockerTest extends junit.JUnit3Suite with ShouldMatchers { outer =>
 
  import replicant._
  import support.{Call, TestCallHandler}
  
  private case class Mock(name: String) 
  
  private val call = Call(Mock("aMock"), "aMethod")
  private val result1 = A(1)
  private val exception = new Exception("testing")
  private val testFailedException = new TestFailedException(1)
  
  @Test def testExpect {
    val callHandler = new TestCallHandler[A] {
      override def update(call: Call, response: => A) {
        call should equal(outer.call(1, "a"))
        this.response = response _
      }
      var response: () => A = _
    }
    val mocker = new Mocker[(Int, String), A](call, callHandler)

    mocker.expect(1, "a") { result1 }
    callHandler.response() should equal(result1)

    mocker.expect(1, "a") { throw exception }
    intercept[Exception] { callHandler.response() } should be theSameInstanceAs(exception)
  } 
  
  @Test def testApply {
    val callHandler = new TestCallHandler[A] {
      override def apply(call: Call): A = {
        call should equal(outer.call(1, "a"))
        response()
      }
      var response: () => A = _
    }
    val mocker = new Mocker[(Int, String), A](call, callHandler)
    
    callHandler.response = () => result1
    mocker(1, "a") should equal(result1)
    
    callHandler.response = () => throw exception
    intercept[Exception] { mocker(1, "a") } should be theSameInstanceAs(exception)
  } 

  @Test def testAssertNotCalled {
    val callHandler = new TestCallHandler[A] {
      override def assertNotCalled {
        called = true
        if (shouldFail) throw testFailedException
      }
      var called = false
      var shouldFail = false
    }
    val mocker = new Mocker[(Int, String), A](call, callHandler)
    
    mocker.assertNotCalled
    callHandler.called should equal(true)
    
    callHandler.shouldFail = true
    intercept[TestFailedException] { mocker.assertNotCalled } should be theSameInstanceAs(testFailedException)
  } 

  @Test def testAssertCalled {
    val callHandler = new TestCallHandler[A] {
      override def assertCalled(call: Call) {
        called = true
        call should equal(outer.call(1, "a"))
        if (shouldFail) throw testFailedException
      }
      var called = false
      var shouldFail = false
    }
    val mocker = new Mocker[(Int, String), A](call, callHandler)
    
    mocker.assertCalled(1, "a")
    callHandler.called should equal(true)
    
    callHandler.shouldFail = true
    intercept[TestFailedException] { mocker.assertCalled(1, "a") } should be theSameInstanceAs(testFailedException)
  } 
  
  @Test def testAssertCalledOnce {
    val callHandler = new TestCallHandler[A] {
      override def assertCalledOnce {
        called = true
        if (shouldFail) throw testFailedException
      }
      var called = false
      var shouldFail = false
    }
    val mocker = new Mocker[(Int, String), A](call, callHandler)
    
    mocker.assertCalledOnce
    callHandler.called should equal(true)
    
    callHandler.shouldFail = true
    intercept[TestFailedException] { mocker.assertCalledOnce } should be theSameInstanceAs(testFailedException)
  } 
  
  @Test def testAssertExpectationsMet {
    val callHandler = new TestCallHandler[A] {
      override def assertExpectationsMet {
        called = true
        if (shouldFail) throw testFailedException
      }
      var called = false
      var shouldFail = false
    }
    val mocker = new Mocker[(Int, String), A](call, callHandler)
    
    mocker.assertExpectationsMet
    callHandler.called should equal(true)
    
    callHandler.shouldFail = true
    intercept[TestFailedException] { mocker.assertExpectationsMet } should be theSameInstanceAs(testFailedException)
  } 

  @Test def testMocker0Equality {
    val mock1 = Mock("mock1")
    val mock2 = Mock("mock2")
    testEqualityOf(   Mocker(mock1, "methodName1") ) .
      shouldEqual(    Mocker(mock1, "methodName1") ).
      shouldNotEqual( Mocker(mock2, "methodName1") ).
      shouldNotEqual( Mocker(mock1, "methodName2") ).
      shouldNotEqual( Mocker0(mock1, "methodName1") ).
      shouldNotEqual( Mocker2(mock1, "methodName1") )
  } 

}