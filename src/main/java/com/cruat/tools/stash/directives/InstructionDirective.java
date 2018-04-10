package com.cruat.tools.stash.directives;

import com.cruat.tools.stash.config.Instruction;

public interface InstructionDirective extends Directive {
	void execute(Instruction i);
}
