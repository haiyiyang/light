package org.haiyiyang.server;

import java.util.ArrayList;
import java.util.List;

import org.haiyiyang.server.UserService;
import org.haiyiyang.server.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.service.annotation.IAmALightService;

@IAmALightService
public class UserServiceImpl implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	public String hello(String name) {
		return "hello " + name;
	}

	public String welcome(String firstName, String lastName) {
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "welcome >>> " + firstName + " " + lastName;
	}

	public UserDto findUser(int userId) {
		return new UserDto(userId);
	}

	public void saveUser(UserDto userDto) {
		LOGGER.info("Saving userDto: " + userDto);
	}

	public List<UserDto> findUserListByGender(boolean gender) {
		List<UserDto> list = new ArrayList<UserDto>();
		for (int i = 0; i < 3; i++) {
			list.add(new UserDto());
		}
		return list;
	}

}
