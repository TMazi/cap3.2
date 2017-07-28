package pl.spring.demo.aop;

import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.aop.MethodBeforeAdvice;

import pl.spring.demo.annotation.SetIdIfNull;
import pl.spring.demo.common.Sequence;
import pl.spring.demo.dao.BookDao;
import pl.spring.demo.to.BookTo;
import pl.spring.demo.to.IdAware;

public class BookDaoSetIdAdvisor implements MethodBeforeAdvice {

	private Sequence sequence;

	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		if (hasAnnotation(method, target, SetIdIfNull.class)) {
			setTheId(args[0], target);
		}

	}

	private void setTheId(Object object, Object target) {
		if (object instanceof IdAware && ((IdAware) object).getId() == null) {
			sequence = new Sequence();
			Collection<? extends IdAware> existingIds = ((BookDao) target).findAll();
			((BookTo) object).setId(sequence.nextValue(existingIds));
		}

	}

	private boolean hasAnnotation(Method method, Object o, Class annotationClazz) throws NoSuchMethodException {
		boolean hasAnnotation = method.getAnnotation(annotationClazz) != null;

		if (!hasAnnotation && o != null) {
			hasAnnotation = o.getClass().getMethod(method.getName(), method.getParameterTypes())
					.getAnnotation(annotationClazz) != null;
		}
		return hasAnnotation;
	}
}
