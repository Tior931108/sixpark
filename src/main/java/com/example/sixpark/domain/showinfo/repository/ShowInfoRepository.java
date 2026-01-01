package com.example.sixpark.domain.showinfo.repository;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowInfoRepository extends JpaRepository<ShowInfo, Integer> {
}
