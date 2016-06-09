package com.code.raker.cmd;

import com.code.metadata.raker.RakerSchema;
import com.code.raker.RakerActor;
import com.code.raker.RakerCmd;
import com.code.raker.RakerRedis;

public abstract class AbstractCmd implements RakerCmd{

	protected RakerActor rakerActor;
	
	protected RakerSchema rakerSchema ;
	
	protected RakerRedis rakerRedis;
	
	protected RakerCmd next;
	
	
	protected String name;
	
	public AbstractCmd(){

	}
	
	public AbstractCmd(RakerActor rakerActor){
		setRakerActor(rakerActor);
	}
	
	public void putRakerCmd(RakerCmd rakerCmd){
		
	}
	
	public void putRakerCmd(RakerCmd.CMD key){
		
	}

	public RakerActor getRakerActor() {
		return rakerActor;
	}

	public void setRakerActor(RakerActor rakerActor) {
		this.rakerActor = rakerActor;
		this.rakerRedis = rakerActor.getRakerRedis();
		this.rakerSchema = rakerActor.getRakerSchema();
	}

	public RakerSchema getRakerSchema() {
		return rakerSchema;
	}

	public void setRakerSchema(RakerSchema rakerSchema) {
		this.rakerSchema = rakerSchema;
	}

	public RakerRedis getRakerRedis() {
		return rakerRedis;
	}

	public void setRakerRedis(RakerRedis rakerRedis) {
		this.rakerRedis = rakerRedis;
	}

	public RakerCmd getNext() {
		return next;
	}

	public void setNext(RakerCmd next) {
		this.next = next;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
	

}
