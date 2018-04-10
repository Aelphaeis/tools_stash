package com.cruat.tools.stash.directives;

import com.cruat.tools.stash.config.Instruction;

public interface TransformDirective extends Directive {
	void execute(Instruction i);
	
}
