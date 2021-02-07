package org.wingsofcarolina.gs.responses;

public class ResponseOk extends AbstractResponse {

	
	public ResponseOk() {
		super();
		super.code(200);
	}

	public ResponseOk(Object entity) {
		super("ok");
		super.code(200);
		super.entity(entity);
	}
}
