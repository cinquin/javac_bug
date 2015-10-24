/**
 * Simple class to reproduce javac bug.
 * Copyright (C) 2014-2015 Olivier Cinquin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;

public class ReproduceJavacBug {
	
	@Target(ElementType.TYPE_USE)
	public @interface Annotation {};
		
	public static void main(String [] args) {
		final Collection<@Annotation Object> list = new ArrayList<>();
		list.stream().forEach(r -> {
			int a = 0;
			System.out.println(a + "");
			list.size();
			throw new RuntimeException();
		});
	}

}
