// Copyright 2011 Kiel Hodges
package replicant

import support._

object Mocker2 {
  def apply[Args1, Args2, Result: ResponseFallback](mock: Any, methodName: String): Mocker2[Args1, Args2, Result] = {
    val call = Call(mock, methodName)
    new Mocker2(call, CallHandler(call, implicitly[ResponseFallback[Result]]))
  }
}

class Mocker2[Args1, Args2, Result] private[replicant] (call: Call, callHandler: CallHandler[Result]) {

  def apply(args1: Args1)(args2: Args2): Result = callHandler(call(args1)(args2))
  def expect(args1: Args1)(args2: Args2)(response: => Result) { callHandler(call(args1)(args2)) = response       }
  def assertCalled(args1: Args1)(args2: Args2)                { callHandler.assertCalled(call(args1)(args2))     }
  def assertCalledOnce                                        { callHandler.assertCalledOnce                     } 
  def assertNotCalled                                         { callHandler.assertNotCalled                      } 
  def assertExpectationsMet                                   { callHandler.assertExpectationsMet                }

}
