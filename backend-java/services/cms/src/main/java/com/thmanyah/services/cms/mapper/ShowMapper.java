package com.thmanyah.services.cms.mapper;

import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ShowCreateUpdateDto;
import com.thmanyah.services.cms.model.dto.ShowDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ShowMapper {

  /**
   * Convert Show entity to ShowDto
   *
   * @param show Show entity
   * @return ShowDto
   */
  ShowDto toDto(Show show);

  /**
   * Convert ShowCreateUpdateDto to Show entity for creation
   *
   * @param userEmail Email of the user creating the show
   * @param dto ShowCreateUpdateDto
   * @return Show entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "createdBy", source = "userEmail")
  @Mapping(target = "updatedBy", source = "userEmail")
  @Mapping(target = "provider", constant = "internal")
  Show toEntity(String userEmail, ShowCreateUpdateDto dto);

  /**
   * Update existing Show entity with data from ShowCreateUpdateDto
   *
   * @param userEmail Email of the user updating the show
   * @param dto ShowCreateUpdateDto
   * @param existingShow Existing Show entity to update
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", source = "userEmail")
  @Mapping(target = "provider", ignore = true)
  void updateEntity(String userEmail, ShowCreateUpdateDto dto, @MappingTarget Show existingShow);
}
