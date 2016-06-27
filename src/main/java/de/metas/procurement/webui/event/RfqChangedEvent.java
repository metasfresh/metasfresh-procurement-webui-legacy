package de.metas.procurement.webui.event;

import com.google.gwt.thirdparty.guava.common.base.Objects;

/*
 * #%L
 * metasfresh-procurement-webui
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

public class RfqChangedEvent implements IApplicationEvent
{
	public static final RfqChangedEvent of(final long rfq_id)
	{
		return new RfqChangedEvent(rfq_id);
	}

	private final long rfq_id;
	
	private RfqChangedEvent(long rfq_id)
	{
		super();
		this.rfq_id = rfq_id;
	}
	
	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
				.add("rfq_id", rfq_id)
				.toString();
	}
	
	public long getRfq_id()
	{
		return rfq_id;
	}
}
