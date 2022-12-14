package com.ssafy.B310.repository;


import com.ssafy.B310.entity.Participation;
import com.ssafy.B310.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Transactional
    List<Participation> findByRoom(Room room);

    @Transactional
    List<Participation> findByroom_roomNum(int room_num);

    @Transactional
    Optional<Participation> findByuser_userNum(int user_num);

    @Transactional
    Optional<Participation> findByuser_userId(String userId);
}
