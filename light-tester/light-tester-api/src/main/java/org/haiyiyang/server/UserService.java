package org.haiyiyang.server;

import java.util.List;

import org.haiyiyang.server.dto.UserDto;

public interface UserService {

	String hello(String name);

	String welcome(String firstName, String lastName);

	UserDto findUser(int userId);

	void saveUser(UserDto userDto);

	List<UserDto> findUserListByGender(boolean gender);

}
