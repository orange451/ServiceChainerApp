/*
 *
 * Copyright (C) 2015-2020 Anarchy Engine Open Source Contributors (see CONTRIBUTORS.md)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 */

package dev.anarchy.event;

import java.util.List;

public class Connection {
	private RunnableArgs function;
	private Event event;

	public Connection(RunnableArgs arg, Event event) {
		this.function = arg;
	}

	public RunnableArgs getFunction() {
		return this.function;
	}

	public void disconnect() {
		function = null;

		if ( event != null ) {
			List<Connection> con = event.connections;
			event = null;
			
			if ( con != null ) {
				con.remove(this);
			}
		}
	}
}
