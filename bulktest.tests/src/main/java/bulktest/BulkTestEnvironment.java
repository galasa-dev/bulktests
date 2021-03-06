/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2021.
 */
package bulktest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@BulkTestManagerField
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface BulkTestEnvironment {

}
