package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.dtos.{ClassOfServiceDto => NewClassOfServiceDto}
import org.bson.types.ObjectId

class ClassOfServiceBuilder extends BaseBuilder {

  def buildDto(classOfService: ClassOfService) = {
    NewClassOfServiceDto(
      Some(classOfService.id.get.toString),
      classOfService.name,
      classOfService.description,
      classOfService.colour,
      classOfService.version,
      None
    )
  }

  def buildEntity(classOfServiceDto: NewClassOfServiceDto) = {
    new ClassOfService(
      {
        if (classOfServiceDto.id != null && classOfServiceDto.id.isDefined) {
          Some(new ObjectId(classOfServiceDto.id.get))
        } else {
          None
        }

      },
      classOfServiceDto.name,
      classOfServiceDto.description,
      classOfServiceDto.colour,
      classOfServiceDto.version
    )
  }

}