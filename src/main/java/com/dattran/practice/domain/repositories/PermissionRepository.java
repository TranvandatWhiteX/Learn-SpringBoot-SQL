package com.dattran.practice.domain.repositories;

import com.dattran.practice.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {}
