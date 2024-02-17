package by.bsuir.dc.rest_basics.services.impl;

import by.bsuir.dc.rest_basics.entities.dtos.request.AuthorRequestTo;
import by.bsuir.dc.rest_basics.entities.dtos.response.AuthorResponseTo;
import by.bsuir.dc.rest_basics.entities.Author;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthorMapper {

    AuthorMapper INSTANCE = Mappers.getMapper( AuthorMapper.class );

    Author requestToModel(AuthorRequestTo authorRequestTo);

    AuthorResponseTo modelToResponse(Author author);

}
