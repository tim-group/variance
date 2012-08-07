package com.youdevise.variance;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class ClassHierarchyInspectorTest {
    
    private static interface Top { }
    private static interface Upper extends Top { }
    private static interface Left extends Upper { }
    private static interface Right extends Upper { }
    private static interface Lower extends Left, Right { }
    private static interface Bottom extends Lower { }

    private ClassHierarchyInspector inspectorOf(Class<?>...classes) {
        return new ClassHierarchyInspector(Lists.newArrayList(classes));
    }
    
    @Test public void
    returns_null_if_no_matching_class_found() {
        ClassHierarchyInspector inspector = inspectorOf();
        assertThat(inspector.nearestClassAssignableFrom(Upper.class), nullValue());
        assertThat(inspector.nearestClassAssignableTo(Upper.class), nullValue());
        assertThat(inspector.nearestSuperclassOf(Upper.class), nullValue());
        assertThat(inspector.nearestSubclassOf(Upper.class), nullValue());
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    nearest_class_assignable_from_X_is_X_if_available() {
        ClassHierarchyInspector inspector = inspectorOf(Top.class, Upper.class);
        
        assertThat(inspector.nearestClassAssignableFrom(Upper.class), equalTo((Class) Upper.class));
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    nearest_superclass_of_X_is_not_X_itself() {
        ClassHierarchyInspector inspector = inspectorOf(Top.class, Upper.class);
        
        assertThat(inspector.nearestSuperclassOf(Upper.class), equalTo((Class) Top.class));
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    nearest_class_assignable_from_X_is_nearest_superclass_of_X_if_X_itself_is_unavailable() {
        ClassHierarchyInspector inspector = inspectorOf(Top.class);
        
        assertThat(inspector.nearestClassAssignableFrom(Upper.class), equalTo((Class) Top.class));
        assertThat(inspector.nearestSuperclassOf(Upper.class), equalTo((Class) Top.class));
    }
    
    @Test(expected=IllegalArgumentException.class) public void
    throws_illegal_argument_exception_if_more_than_one_superclass_is_nearest() {
        ClassHierarchyInspector inspector = inspectorOf(Left.class, Right.class);
        
        inspector.nearestClassAssignableFrom(Lower.class);
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    finds_nearest_superclass_even_if_its_ancestry_is_ambiguous() {
        ClassHierarchyInspector inspector = inspectorOf(Left.class, Right.class, Lower.class);
        
        assertThat(inspector.nearestClassAssignableFrom(Bottom.class), equalTo((Class) Lower.class));
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    nearest_class_assignable_to_X_is_X_if_available() {
        ClassHierarchyInspector inspector = inspectorOf(Lower.class, Bottom.class);
        
        assertThat(inspector.nearestClassAssignableTo(Lower.class), equalTo((Class) Lower.class));
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    nearest_subclass_of_X_is_not_X_itself() {
        ClassHierarchyInspector inspector = inspectorOf(Lower.class, Bottom.class);
        
        assertThat(inspector.nearestSubclassOf(Lower.class), equalTo((Class) Bottom.class));
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    nearest_class_assignable_to_X_is_nearest_subclass_of_X_if_X_itself_is_unavailable() {
        ClassHierarchyInspector inspector = inspectorOf(Bottom.class);
        
        assertThat(inspector.nearestClassAssignableTo(Lower.class), equalTo((Class) Bottom.class));
        assertThat(inspector.nearestSubclassOf(Lower.class), equalTo((Class) Bottom.class));
    }
    
    @Test(expected=IllegalArgumentException.class) public void
    throws_illegal_argument_exception_if_more_than_one_subclass_is_nearest() {
        ClassHierarchyInspector inspector = inspectorOf(Left.class, Right.class);
        
        inspector.nearestClassAssignableTo(Upper.class);
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Test public void
    finds_nearest_subclass_even_if_its_descendency_is_ambiguous() {
        ClassHierarchyInspector inspector = inspectorOf(Left.class, Right.class, Upper.class);
        
        assertThat(inspector.nearestClassAssignableTo(Top.class), equalTo((Class) Upper.class));
    }
    
}
