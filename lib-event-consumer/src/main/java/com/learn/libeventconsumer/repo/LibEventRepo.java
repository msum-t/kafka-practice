package com.learn.libeventconsumer.repo;

import com.learn.libeventconsumer.entity.LibaryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibEventRepo extends JpaRepository<LibaryEvent,Integer> {
}
