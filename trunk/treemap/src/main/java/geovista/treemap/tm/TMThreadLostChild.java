/*
 * TMThreadLostChild.java
 * www.bouthier.net
 *
 * The MIT License :
 * -----------------
 * Copyright (c) 2001 Christophe Bouthier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package geovista.treemap.tm;

import java.util.logging.Logger;

/**
 * The TMThreadLostChild implements a thread that removes a child to a
 * TMNodeModel.
 * 
 * @author Christophe Bouthier [bouthier@loria.fr]
 * 
 */
class TMThreadLostChild extends TMThreadModel {

	private TMNode parent = null; // the node which losts a child
	private TMNode child = null; // the lost child node

	final static Logger logger = Logger.getLogger(TMThreadLostChild.class
			.getName());

	/**
	 * Constructor.
	 * 
	 * @param status
	 *            the status view for feedback
	 * @param model
	 *            the TMNodeModelRoot
	 * @param view
	 *            the view to update
	 * @param parent
	 *            the parent node
	 * @param child
	 *            the child node
	 */
	TMThreadLostChild(TMStatusView status, TMNodeModelRoot model, TMView view,
			TMNode parent, TMNode child) {
		super(status, model, view);
		this.parent = parent;
		this.child = child;
	}

	/**
	 * Removess the new child.
	 */
	@Override
	void task() {
		status.setStatus(new TMSDSimple("Removing a child ..."));
		TMNodeModel parentCandidate = model.nodeContaining(parent);
		if (parentCandidate == null) {
			logger.info("can't remove from null parent");
			return;
			// throw new TMExceptionUnknownTMNode(parent);
		} else if (!(parentCandidate instanceof TMNodeModelComposite)) {
			throw new TMExceptionLeafTMNode(parent);
		}
		TMNodeModelComposite parentNode = (TMNodeModelComposite) parentCandidate;

		TMNodeModel childNode = model.nodeContaining(child);
		if (childNode == null) {
			throw new TMExceptionUnknownTMNode(child);
		}

		parentNode.lostChild(childNode);

		model.computeSize();
		status.setStatus(new TMSDSimple("Child removed"));
	}

}
