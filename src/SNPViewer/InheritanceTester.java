package SNPViewer;

public class InheritanceTester extends tester {

	public InheritanceTester() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	protected void testA(){
		testB();
	}
	
	protected void testC(){
		System.out.println("C++");
	}
}
