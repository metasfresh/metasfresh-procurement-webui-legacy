package de.metas.procurement.webui.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.metas.procurement.sync.protocol.SyncRfQ;
import de.metas.procurement.sync.protocol.SyncRfQQty;
import de.metas.procurement.webui.model.BPartner;
import de.metas.procurement.webui.model.Rfq;
import de.metas.procurement.webui.model.RfqQty;
import de.metas.procurement.webui.repository.RfqQtyRepository;
import de.metas.procurement.webui.repository.RfqRepository;

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
@Transactional
public class SyncRfqImportService extends AbstractSyncImportService
{
	@Autowired
	RfqRepository rfqRepo;
	@Autowired
	RfqQtyRepository rfqQtyRepo;

	public void importRfQs(final BPartner bpartner, final List<SyncRfQ> syncRfQs)
	{
		for (final SyncRfQ syncRfQ : syncRfQs)
		{
			importRfQ(bpartner, syncRfQ);
		}

	}

	private Rfq importRfQ(final BPartner bpartner, final SyncRfQ syncRfQ)
	{
		final Rfq rfq = importRfQNoCascade(bpartner, syncRfQ);
		if (rfq == null)
		{
			return null;
		}

		//
		// RfQ Quantities
		final List<RfqQty> rfqQuantitiesToSave = new ArrayList<>();
		final Map<String, RfqQty> rfqQuantities = mapByUuid(rfq.getQuantities());
		for (final SyncRfQQty syncRfQQty : syncRfQ.getQuantities())
		{
			// If delete request, skip importing the RfQ quantity.
			// As a result, the RfqQty will be deleted below.
			if (syncRfQQty.isDeleted())
			{
				continue;
			}

			final RfqQty rfqQuantityExisting = rfqQuantities.remove(syncRfQ.getUuid());
			final RfqQty rfqQuantity = importRfQQuantityNoSave(rfq, syncRfQQty, rfqQuantityExisting);
			if (rfqQuantity == null)
			{
				continue;
			}

			rfqQuantitiesToSave.add(rfqQuantity);
		}

		//
		// Delete remaining RfQ Quantities
		for (final RfqQty rfqQuantity : rfqQuantities.values())
		{
			deleteRfQQuantity(rfqQuantity);
		}

		//
		// Save created/updated lines
		rfqQtyRepo.save(rfqQuantitiesToSave);

		return rfq;
	}

	private Rfq importRfQNoCascade(final BPartner bpartner, final SyncRfQ syncRfQ)
	{
		final String uuid = syncRfQ.getUuid();
		Rfq rfq = rfqRepo.findByUuid(uuid);
		if (rfq == null)
		{
			rfq = new Rfq();
			rfq.setUuid(uuid);
			rfq.setBpartner(bpartner);
		}

		rfq.setDeleted(false);

		rfq.setDateStart(syncRfQ.getDateStart());
		rfq.setDateEnd(syncRfQ.getDateEnd());
		rfq.setName(syncRfQ.getName());

		rfq.setDateClose(syncRfQ.getDateClose());
		rfq.setClosed(syncRfQ.isClosed());
		rfq.setWinner(syncRfQ.isWinner());

		rfq.setProduct_uuid(syncRfQ.getProduct_uuid());

		rfq.setQtyRequested(syncRfQ.getQtyRequested());
		rfq.setPricePromised(syncRfQ.getPricePromised());

		rfqRepo.save(rfq);
		logger.debug("Imported: {} -> {}", syncRfQ, rfq);

		return rfq;
	}

	private RfqQty importRfQQuantityNoSave(final Rfq rfq, final SyncRfQQty syncRfQQty, final RfqQty rfqQuantityExisting)
	{
		RfqQty rfqQuantity = rfqQuantityExisting;

		final String uuid = syncRfQQty.getUuid();
		if (rfqQuantity != null && !Objects.equals(uuid, rfqQuantity.getUuid()))
		{
			rfqQuantity = null;
		}

		if (rfqQuantity == null)
		{
			rfqQuantity = new RfqQty();
			rfqQuantity.setUuid(uuid);
			rfqQuantity.setRfq(rfq);
		}

		rfqQuantity.setDeleted(false);
		rfqQuantity.setDatePromised(syncRfQQty.getDatePromised());
		rfqQuantity.setQtyPromised(syncRfQQty.getQtyPromised());

		logger.debug("Imported: {} -> {}", syncRfQQty, rfqQuantity);

		return rfqQuantity;
	}

	private void deleteRfQQuantity(final RfqQty rfqQuantity)
	{
		rfqQtyRepo.delete(rfqQuantity);
		rfqQtyRepo.flush();
		logger.debug("Deleted: {}", rfqQuantity);
	}
}
