package com.tw.ioc;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by pzzheng on 12/20/16.
 */
public class RecordBinder implements Binder {
    private final HashMap<Class<?>, Binding<?>> mutableBindings;
    private final HashMap<Class<? extends Annotation>, ScopeBinding> scopeBindings;
//    private HashMap<Class<?>, Binding<?>> immutableBindings = Collections.unmodifiableMap(mutableBindings);

    public RecordBinder() {
        this.mutableBindings = new HashMap<>();
        this.scopeBindings = new HashMap();
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(Class<T> toInjectClass) {
        Binding<T> binding = new Binding<>(toInjectClass, null, null, getScope(toInjectClass));
        mutableBindings.put(toInjectClass, binding);
        return new BindingBuilderImpl(toInjectClass, mutableBindings);
    }

    private <T> Scope getScope(Class<T> toInjectClass) {
        Annotation[] annotations = toInjectClass.getAnnotations();
        Optional<Annotation> scopeAnnotation = Arrays.stream(annotations).filter(annotation -> annotation.annotationType().isAnnotationPresent(javax.inject.Scope.class)).findFirst();
        Scope scope = null;
        if(scopeAnnotation.isPresent()) {
            scope = scopeBindings.get(scopeAnnotation.get().annotationType()).getScope();
        }
        return scope;
    }

    @Override
    public <T> Binding<T> getBinding(Class<T> toInjectClass) {
        return (Binding<T>) mutableBindings.get(toInjectClass);
    }

    @Override
    public void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope) {
        scopeBindings.put(scopeAnnotation, new ScopeBinding(scopeAnnotation, scope));
    }
}
