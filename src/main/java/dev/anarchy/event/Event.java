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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {
	protected List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
	protected List<Connection> disconnectQueue = Collections.synchronizedList(new ArrayList<Connection>());
	protected List<Thread> waitingThreads = Collections.synchronizedList(new ArrayList<Thread>());
	
	public void wait(Thread thread) {
		synchronized(waitingThreads) {
			Thread.yield();
			waitingThreads.add(thread);
		}
	}
	
	public Connection connect(RunnableArgs runnableArgs) {
		Connection cnt = new Connection(runnableArgs, Event.this);
		synchronized(connections) {
			connections.add(cnt);
		}
		
		return cnt;
	}
	
	public void fire() {
		fire(new Object[] {});
	}

	@SuppressWarnings("deprecation")
	public void fire(Object...args) {
		synchronized(connections) {
			// Remove the queued connections
			synchronized( disconnectQueue ) {
				while ( disconnectQueue.size() > 0 ) {
					Connection c = disconnectQueue.get(0);
					connections.remove(c);
					disconnectQueue.remove(c);
				}
			}
			
			// Fire remaining connections
			int len = connections.size();
			for (int i = 0; i < len; i++) {
				if ( i >= connections.size() )
					continue;
				Connection temp = connections.get(i);
				if ( temp == null )
					continue;
				
				try {
					temp.getFunction().run(args);
				} catch(Exception e) {
					//
				}
			}
		}
		
		synchronized(waitingThreads) {
			for (Thread thread : waitingThreads) {
				thread.resume();
			}
			waitingThreads.clear();
		}
	}

	public void disconnect(Connection value) {
		disconnectQueue.add(value);
	}

	public void disconnectAll() {
		synchronized(connections) {
			connections.clear();
			disconnectQueue.clear();
		}
	}

	public static void fireEvent(Event event, Object...values) {
		if ( event == null )
			return;
		
		event.fire(values);
	}
}
