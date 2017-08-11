package com.nmerris.roboresumedb.repositories;

import com.nmerris.roboresumedb.models.Skill;
import org.springframework.data.repository.CrudRepository;

public interface SkillsRepo extends CrudRepository<Skill, Long> {

}