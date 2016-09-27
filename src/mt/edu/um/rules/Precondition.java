package mt.edu.um.rules;

public class Precondition<I> {

	private String _targetMethod;
	private Object[] _methodArgs;
	private Class<?>[] _methodParams;
	private String _method;
	private Operator _operator;
	private Object _expectedValue;

	public Precondition() {
		this._operator = BasicOperator.EQ;
	}

	public void setMethodToCall(String method) {
		this._method = method;
	}

	public void setExpectedValue(Object value) {
		this._expectedValue = value;
	}

	public void setMethodInvocationTarget(String methodName) {
		this._targetMethod = methodName;
	}

	public void setOperator(BasicOperator operator) {
		this._operator = operator;
	}

	public void setMethodArgs(Object... args) {
		this._methodArgs = args;
		this._methodParams = new Class<?>[args.length];

		for (int i = 0; i < args.length; i++) {
			this._methodParams[i] = args[i].getClass();
		}

	}

	public Object[] getMethodArgs() {
		return this._methodArgs;
	}

	public boolean evaluate(I object) throws Exception {
		boolean success = false;

		if (this._method != null) {
			Object target;

			if (this._targetMethod != null) {
				target = object.getClass().getMethod(this._targetMethod).invoke(object);

			} else {
				target = object;
			}

			Object value = null;

			if (target != null) {
				if (this._methodArgs != null && this._methodArgs.length > 0) {
					value = target.getClass().getMethod(this._method,
							this._methodParams)
							.invoke(target, this._methodArgs);
				} else {
					value = target.getClass().getMethod(this._method).invoke(
							target);
				}
			}

			success = this._operator.evaluate(value, this._expectedValue);
		}

		return success;
	}
	
	public String toString() {
		return this._operator + "[" + this._expectedValue + "]";
	}
}
