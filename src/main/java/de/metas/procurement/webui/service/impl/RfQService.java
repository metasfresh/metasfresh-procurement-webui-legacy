package de.metas.procurement.webui.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableMap;

import de.metas.procurement.webui.model.BPartner;
import de.metas.procurement.webui.model.Rfq;
import de.metas.procurement.webui.model.RfqQty;
import de.metas.procurement.webui.model.User;
import de.metas.procurement.webui.repository.RfqQtyRepository;
import de.metas.procurement.webui.repository.RfqRepository;
import de.metas.procurement.webui.service.IRfQService;
import de.metas.procurement.webui.util.DateUtils;

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

@Service
public class RfQService implements IRfQService
{
	@Autowired
	private RfqRepository rfqRepo;
	@Autowired
	private RfqQtyRepository rfqQuantityRepo;

	@Override
	public List<Rfq> getActiveRfQs(final User user)
	{
		final BPartner bpartner = user.getBpartner();
		// TODO FRESH-402: filter only active ones!
		return rfqRepo.findByBpartner(bpartner);
	}

	@Override
	public Map<Date, RfqQty> getRfQQuantitiesIndexedByDay(final Rfq rfq)
	{
		final Map<Date, RfqQty> day2qty = new HashMap<>();
		for (final RfqQty qty : rfqQuantityRepo.findByRfq(rfq))
		{
			final Date day = DateUtils.truncToDay(qty.getDatePromised());
			day2qty.put(day, qty);
		}
		
		return ImmutableMap.copyOf(day2qty);
	}
}
