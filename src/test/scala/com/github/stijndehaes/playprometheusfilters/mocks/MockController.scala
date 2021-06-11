package com.github.stijndehaes.playprometheusfilters.mocks

import play.api.mvc.{ AbstractController, ControllerComponents }

import javax.inject.Inject
import play.api.mvc
import play.api.mvc.AnyContent

class MockController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def ok: mvc.Action[AnyContent] =
    Action {
      Ok("ok")
    }

  def error: mvc.Action[AnyContent] =
    Action {
      NotFound("error")
    }

}
