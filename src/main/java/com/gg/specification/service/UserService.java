package com.gg.specification.service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gg.specification.entity.Task;
import com.gg.specification.entity.TaskStatus;
import com.gg.specification.entity.User;
import com.gg.specification.entity.UserStatus;
import com.gg.specification.repository.TaskRepository;
import com.gg.specification.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	

	/**
	 * Join specification 
	 * 
	 * It generate following sql query:
	 *  select u.* from specification_demo.user u inner join specification_demo.task t on u.id=t.user_id 
	 *  where u.status='ENABLED' and t.status='PENDING' 
	 *  
	 * @return
	 */
	//
	public Page<User> getUsers() {
		Specification<User> specification = (root, criteriaQuery, criteriaBuilder) -> {
			
			Join<User, Task> taskJoin = root.join("tasks");
			
			Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), UserStatus.ENABLED);
			
			Predicate taskStatusPredicate = criteriaBuilder.equal(taskJoin.get("status"), TaskStatus.PENDING);
			
			criteriaQuery.distinct(true);
			
			return criteriaBuilder.and(statusPredicate, taskStatusPredicate);
		};

		return userRepository.findAll(specification, PageRequest.of(0, 1));
	}
	
	
	
	/**
	 * Subquery specification 
	 * 
	 * It generate following query
	 *   
	 *   select distinct u.id as id1_1_, u.age , u.email , u.first_name , u.last_name , u.status
	 *   from specification_demo.user u where u.status=? and (select count(t.id) from specification_demo.task t
	 *    where t.status=‘PENDING’ and u.id=t.user_id)>1 
	 * 
	 * @return
	 */

	public Page<User> getUsers1() {
		Specification<User> specification = (root, criteriaQuery, criteriaBuilder) -> {
           
			Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), UserStatus.ENABLED);
			
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			
			Root<Task> taskRoot = subquery.from(Task.class);
			
			subquery.select(criteriaBuilder.count(taskRoot));
			
			Predicate taskStatusPredicate = criteriaBuilder.equal(taskRoot.get("status"), TaskStatus.PENDING);
			
			Predicate userPredicate = criteriaBuilder.equal(root.get("id"), taskRoot.get("user").get("id"));
			
			subquery.where(taskStatusPredicate, userPredicate);
			
			Predicate taskCount = criteriaBuilder.greaterThanOrEqualTo(subquery, 1l);
			
		    criteriaQuery.distinct(true);
			
		   return criteriaBuilder.and(statusPredicate, taskCount);
		
		};

		return userRepository.findAll(specification, PageRequest.of(0, 1));
	}
	
	/**
	 * Subquery and join specification 
	 *
	 * select * from specification_demo.task t inner join specification_demo.user u on t.user_id=u.id 
	 * where t.status='PENDING' and (t.user_id in (select u.id from specification_demo.user u where u.status='ENABLED')) 
	 * 
	 * @return
	 */
	public Page<Task> getTasks() {
		Specification<Task> specification = (root, criteriaQuery, criteriaBuilder) -> {
			
			Join<Task,User> userJoin = root.join("user");
			
			Subquery<User> userSubquery = criteriaQuery.subquery(User.class);
			
			Predicate statusPredicate = criteriaBuilder.equal(userJoin.get("status"), UserStatus.ENABLED);
			
			userSubquery.select(userJoin.get("id"));
			userSubquery.from(User.class);
			userSubquery.where(statusPredicate);
			
			Predicate taskStatusPredicate = criteriaBuilder.equal(root.get("status"), TaskStatus.PENDING);
			
			Predicate userIdIn = criteriaBuilder.in(root.get("user").get("id")).value(userSubquery);
		
			
			return criteriaBuilder.and(taskStatusPredicate, userIdIn);
		};

		return taskRepository.findAll(specification, PageRequest.of(0, 1));
	}
	
}
