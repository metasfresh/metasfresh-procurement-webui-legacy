package de.metas.procurement.webui.sync;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.metas.procurement.sync.protocol.SyncRfQ;
import de.metas.procurement.webui.model.BPartner;
import de.metas.procurement.webui.model.Product;
import de.metas.procurement.webui.model.Rfq;
import de.metas.procurement.webui.repository.BPartnerRepository;
import de.metas.procurement.webui.repository.ProductRepository;
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
	@Autowired
	ProductRepository productRepo;
	@Autowired
	@Lazy
	BPartnerRepository bpartnerRepo;

	public void importRfQs(final BPartner bpartner, final List<SyncRfQ> syncRfQs)
	{
		for (final SyncRfQ syncRfQ : syncRfQs)
		{
			importRfQ(bpartner, syncRfQ);
		}
	}

	public Rfq importRfQ(final SyncRfQ syncRfQ)
	{
		final BPartner bpartner = null;
		return importRfQ(bpartner, syncRfQ);
	}

	public Rfq importRfQ(BPartner bpartner, final SyncRfQ syncRfQ)
	{
		final String uuid = syncRfQ.getUuid();
		Rfq rfq = rfqRepo.findByUuid(uuid);
		if (rfq == null)
		{
			rfq = new Rfq();
			rfq.setUuid(uuid);
			
			if(bpartner == null)
			{
				bpartner = bpartnerRepo.findByUuid(syncRfQ.getBpartner_uuid());
			}
			rfq.setBpartner(bpartner);
		}

		rfq.setDeleted(false);

		rfq.setDateStart(syncRfQ.getDateStart());
		rfq.setDateEnd(syncRfQ.getDateEnd());

		rfq.setDateClose(syncRfQ.getDateClose());
		rfq.setClosed(syncRfQ.isClosed());
		rfq.setWinner(syncRfQ.isWinner());
		
		final Product product = productRepo.findByUuid(syncRfQ.getProduct_uuid());
		// FIXME: throw ex if null
		rfq.setProduct(product);

		rfq.setQtyRequested(syncRfQ.getQtyRequested());

		rfqRepo.save(rfq);
		logger.debug("Imported: {} -> {}", syncRfQ, rfq);

		return rfq;
	}
}
