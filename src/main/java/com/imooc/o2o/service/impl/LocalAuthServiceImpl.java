package com.imooc.o2o.service.impl;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import com.imooc.o2o.dao.PersonInfoDao;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.exceptions.WechatAuthOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.LocalAuthDao;
import com.imooc.o2o.dto.LocalAuthExecution;
import com.imooc.o2o.entity.LocalAuth;
import com.imooc.o2o.enums.LocalAuthStateEnum;
import com.imooc.o2o.exceptions.LocalAuthOperationException;
import com.imooc.o2o.service.LocalAuthService;
import com.imooc.o2o.util.MD5;

@Service
public class LocalAuthServiceImpl implements LocalAuthService {

	@Autowired
	private LocalAuthDao localAuthDao;

	@Autowired
	private PersonInfoDao personInfoDao;

	@Override
	public LocalAuth getLocalAuthByUsernameAndPwd(String username, String password) {
		return localAuthDao.queryLocalByUserNameAndPwd(username, MD5.getMd5(password));
	}


	@Override
	public LocalAuth getLocalAuthByUsername(String username) {
        LocalAuth localAuth = localAuthDao.queryLocalByUserName(username);
        return localAuth;
	}

	@Override
	public int setLocalAuthByUsernameAndPwd(String userName, String password) {
		PersonInfo personInfo = new PersonInfo();
		personInfo.setName(userName);
		personInfo.setEnableStatus(1);
		personInfo.setUserType(1);
		personInfo.setGender("1");
		Long userId = getRandomLong();
		personInfo.setUserId(userId);
		int effectedNum = personInfoDao.insertPersonInfo(personInfo);
		if (effectedNum <= 0) {
			throw new WechatAuthOperationException("添加用户信息失败");
		}
		LocalAuth localAuth = new LocalAuth();
		localAuth.setCreateTime(new Date());
		localAuth.setLastEditTime(new Date());
		// 对密码进行MD5加密
		localAuth.setPassword(MD5.getMd5(password));
		localAuth.setUsername(userName);
		localAuth.setPersonInfo(personInfo);
		int effectedNum2 = localAuthDao.insertLocalAuth(localAuth);
		return effectedNum2;
	}

	private Long getRandomLong() {
		Random random = new Random();
		int i = random.nextInt(9000) + 1000;
		Long aLong = new Long((long) 16);
		return aLong;
	}

	@Override
	public LocalAuth getLocalAuthByUserId(long userId) {
		return localAuthDao.queryLocalByUserId(userId);
	}

	@Override
	@Transactional
	public LocalAuthExecution bindLocalAuth(LocalAuth localAuth) throws LocalAuthOperationException {
		// 空值判断，传入的localAuth 帐号密码，用户信息特别是userId不能为空，否则直接返回错误
		if (localAuth == null || localAuth.getPassword() == null || localAuth.getUsername() == null
				|| localAuth.getPersonInfo() == null || localAuth.getPersonInfo().getUserId() == null) {
			return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
		}
		// 查询此用户是否已绑定过平台帐号
		LocalAuth tempAuth = localAuthDao.queryLocalByUserId(localAuth.getPersonInfo().getUserId());
		if (tempAuth != null) {
			// 如果绑定过则直接退出，以保证平台帐号的唯一性
			return new LocalAuthExecution(LocalAuthStateEnum.ONLY_ONE_ACCOUNT);
		}
		try {
			// 如果之前没有绑定过平台帐号，则创建一个平台帐号与该用户绑定
			localAuth.setCreateTime(new Date());
			localAuth.setLastEditTime(new Date());
			// 对密码进行MD5加密
			localAuth.setPassword(MD5.getMd5(localAuth.getPassword()));
			int effectedNum = localAuthDao.insertLocalAuth(localAuth);
			// 判断创建是否成功
			if (effectedNum <= 0) {
				throw new LocalAuthOperationException("帐号绑定失败");
			} else {
				return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuth);
			}
		} catch (Exception e) {
			throw new LocalAuthOperationException("insertLocalAuth error: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public LocalAuthExecution modifyLocalAuth(Long userId, String userName, String password, String newPassword)
			throws LocalAuthOperationException {
		// 非空判断，判断传入的用户Id,帐号,新旧密码是否为空，新旧密码是否相同，若不满足条件则返回错误信息
		if (userId != null && userName != null && password != null && newPassword != null
				&& !password.equals(newPassword)) {
			try {
				// 更新密码，并对新密码进行MD5加密
				int effectedNum = localAuthDao.updateLocalAuth(userId, userName, MD5.getMd5(password),
						MD5.getMd5(newPassword), new Date());
				// 判断更新是否成功
				if (effectedNum <= 0) {
					throw new LocalAuthOperationException("更新密码失败");
				}
				return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS);
			} catch (Exception e) {
				throw new LocalAuthOperationException("更新密码失败:" + e.toString());
			}
		} else {
			return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
		}
	}

}
