package com.cruat.tools.stash.directives.xml;

import com.cruat.tools.stash.config.Instruction;
import com.cruat.tools.stash.directives.Supports;
import com.cruat.tools.stash.directives.TransformDirective;
import com.cruat.tools.stash.transformer.Transformer;
import com.cruat.tools.stash.transformer.XmlTransformer;

@Supports({ XmlTransformer.class })
public class Remove implements TransformDirective{
	@Override
	public void execute(Instruction i, Transformer t) {
		// TODO Auto-generated method stub
		
	}
}
