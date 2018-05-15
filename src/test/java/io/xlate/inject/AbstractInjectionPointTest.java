/*******************************************************************************
 * Copyright (C) 2018 xlate.io LLC, http://www.xlate.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package io.xlate.inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

abstract class AbstractInjectionPointTest {

    @SuppressWarnings("unchecked")
    protected <T extends Annotation> InjectionPoint mockInjectionPoint(Class<T> annotationType,
                                                                       T annotation,
                                                                       Type type,
                                                                       Class<? extends Member> memberType,
                                                                       String memberName,
                                                                       int memberPosition) {
        InjectionPoint injectionPoint = mock(InjectionPoint.class);

        Member member = mock(memberType);
        when(injectionPoint.getType()).thenReturn(type);
        when(injectionPoint.getMember()).thenReturn(member);
        when(member.getName()).thenReturn(memberName);

        @SuppressWarnings("rawtypes")
		Class declaringClass = getClass();
        when(member.getDeclaringClass()).thenReturn(declaringClass);

        @SuppressWarnings("rawtypes")
        Bean mockBean = mock(Bean.class);
        when(injectionPoint.getBean()).thenReturn(mockBean);
        when(mockBean.getBeanClass()).thenReturn(getClass());

        AnnotatedParameter<?> annotated = mock(AnnotatedParameter.class);
        when(annotated.getPosition()).thenReturn(memberPosition);
        when(injectionPoint.getAnnotated()).thenReturn(annotated);

        when(annotated.getAnnotation(annotationType)).thenReturn(annotation);

        return injectionPoint;
    }

}
