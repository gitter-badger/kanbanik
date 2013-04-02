package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.dto.ManipulateUserDto
import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.model.User

class EditUserCommand extends ServerCommand[SimpleParams[ManipulateUserDto], FailableResult[SimpleParams[UserDto]]] with CredentialsUtils {

  lazy val userBuilder = new UserBuilder

  def execute(params: SimpleParams[ManipulateUserDto]): FailableResult[SimpleParams[UserDto]] = {

    if (!isAuthenticated(params.getPayload().getUserName(), params.getPayload().getPassword())) {
      new FailableResult(new SimpleParams(new UserDto), false, "The credentials provided are not correct!")
    } else {
      val user = User.byId(params.getPayload().getUserName())
      val hashedPass: (String, String) = hashPassword(params.getPayload().getNewPassword())
      val newUser = user.withPassword(hashedPass._1, hashedPass._2).withRealName(params.getPayload().getRealName()).withVersion(params.getPayload().getVersion())

      new FailableResult(new SimpleParams(userBuilder.buildDto(newUser.store)))
    }
  }
}