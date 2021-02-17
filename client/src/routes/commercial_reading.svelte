<script>
	import { onMount } from 'svelte'
	import { goto } from '@sapper/app'
	import { notifier } from '@beyonk/svelte-notifications'
	import { user } from '../store.js'
  import Class from "../components/Class.svelte";
  import Product from "../components/Product.svelte";

	var data = null;

	onMount(async () => {
		getUser();
	});

	const getUser = async () => {
		const response = await fetch('/api/user', {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json'
			}
		});
		if (!response.ok) {
			if (response.status == 404) {
				// User was simpoly not found, therefore not authenticated
				goto('login');
			} else {
				// Otherwise, something else went wrong
				notifier.danger('Retrieve of user information failed.');
			}
		} else {
			var tmp = await response.json();
			user.set(tmp);
		}
	}
</script>

<svelte:head>
	<title>WCFC Commercial Materials</title>
</svelte:head>

<div class="article">
<div class="article_title">WCFC Commercial Pilot Ground School Class Supplies, January 2021</div>
<div class="author">George Scheer</div>
<div class="label">Chief Flight Instructor</div>
<div class="label">Wings of Carolina</div>

<p>The Syllabus for the Commercial Certificate Ground School and the many supporting
articles will be distributed to participants via email and will also be posted
and available for quick download from the WCFC Ground School website.</p>

<p>Here is a list of the required books and materials for the Commercial Certificate
Ground School. The club will have available from our pilot supplies inventory
these books and tools for purchase at our cost. We do not endeavor to make a
profit on these books – they are available for purchase from the club at cost as
a service to our members and ground school participants. We will stock the
current editions. You will want the latest edition of some of these; for others
an older edition will suffice. Some things change; some things seldom do. The
text from Jeppesen should be a current edition; similarly the test prep from
ASA, obviously the FAR/AIM, and the Weather Services. The weather theory doesn’t
have to be the most current. Weather forecasting has improved drastically in
recent years, but our understanding of weather at this level has been well
understood for a few years. See my notes inline.</p>

<p>For those students who find it impossible to visit the club and purchase
directly from the club, I have provided here links to online purchase of these
required materials. The links and prices are current as of January 2021.
Wherever possible, I have included hyperlinks to what I believe would be the
current edition of these publications, usually from Amazon. They are available
elsewhere. Feel free to shop. I just wanted to simplify as much as possible.</p>

<p>Note: ASA is a publisher of aviation books and material. Along with their
original publications, they reprint FAA documents in book form.  Other
commercial publishers do the same, since these are government documents.  Many
of these are available for reading or download from the FAA site for free. You
can reference them at <a href="https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/">
this FAA page</a>. I will include links to the locations of
these documents on the FAA website.  Depending on how you like to work and the
tools you have available, it is a reasonable option.  I have multiple published
editions of the FAA manuals, but I keep a digital copy of the current editions
from the FAA website downloaded on an Ipad for reference. I use the Goodreader
application to organize and view these documents.  You will have your preferred
tool.  </p>


<div class="subtitle">Required books:</div>

<Product title="Jeppesen Guided Flight Discovery Instrument/Commercial (10001784-005) Date: 2016"
	pub="Jeppesen"
	notes="Available for $79.95 from MyPilotStore. Our primary text for the WCFC
	Commercial Certificate Ground School will be the Guided Flight Discovery
	INSTRUMENT / COMMERCIAL manual, published by Jeppesen. You will need a copy of
	this text. We will be using the 2016 edition, the latest edition available as
	a bound book. The current edition is ideal but any edition since 2012 should
	suffice.  The WCFC has the most recent edition, c. 2016, for sale at cost."
	url="https://www.mypilotstore.com/MyPilotStore/sep/1270?gclid=CjwKCAiAo5qABhBdEiwAOtGmbtDwkVm1L2liLEhAQ7yH74dhmhfKwVB3pqoFsgAaFQMVhxfkwQNysRoCpkoQAvD_BwE"
/>

<Product title="Jeppesen Guided Flight Discovery Instrument/Commercial (10001784-005) Date: 2016 E-book"
	pub="Jeppesen"
	notes="The Jeppesen website lists only this E-book version as its most current
	edition. $73.99 as an E-book. It appears to be the same 2016 edition available
	in book form from MyPilotStore. Amazon lists many volumes of the Instrument /
	Commercial text, but some are obviously out of date and it is difficult to
	ascertain which edition you are purchasing. And the used inventory constantly
	changes, so I cannot provide a specific link. There are many available used
	for much less than the new copies. Search for a 2016 edition if you can find
	it. I would want a volume no older than about 2015 because there have been
	many changes in instrument procedures in recent years. You might choose an
	inexpensive used copy from Amazon, knowing that it will be out of date. Most
	of it will be applicable. This is not an FAA publication and so is not
	available from the FAA."
	url="https://shop.jeppesen.com/All-Products/Training-and-Courseware/Training-Type/E-Books/Instrument-Commercial-E-book/p/10277281"
/>

<Product title="2021 Commercial Pilot Test Prep"
	notes="Available from Amazon for $22.17. To prepare for the FAA Knowledge
	Exam, you will need the 2021 Commercial Pilot Test Prep, published by ASA
	available for $22.17 from Amazon as of February 2021.  This is not an FAA
	document and so is not available from the FAA website."
	url="https://www.amazon.com/Commercial-Pilot-Test-Prep-2021/dp/1619549697/ref=asc_df_1619549697/?tag=hyprod-20&linkCode=df0&hvadid=459680186284&hvpos=&hvnetw=g&hvrand=13948897091875711809&hvpone=&hvptwo=&hvqmt=&hvdev=c&hvdvcmdl=&hvlocint=&hvlocphy=9009672&hvtargid=pla-929954180065&psc=1"
/>

<div class="subtitle">Recommended books:</div>

<Product title="FAR/AIM"
	pub="ASA 2021 edition (or subsequent edition as available)"

	notes='Available from Amazon for $14.90.   The club keeps current copies of
	the FAR/AIM volume in inventory.<br>
	<br>
	The Code of Federal Regulations is the codification of regulations established
	by the agencies of the Federal Government.  It contains 50 Titles, one of
	which, Title 14 Aeronautics and Space, dictates the regulatory environment
	under which we fly. This volume contains those Parts of Title 14 that are most
	relevant to aviation and also the Aeronautical Information Manual, which is
	guidance from the FAA pertinent to aviation.  This volume is available in book
	form for $14.90 as an ASA reprint of the FAR/AIM.<br>
	<br>
	Instrument flying is highly procedural and a sound understanding of the
	regulations that govern the IFR system is essential.  Regulations are not the
	fun part of flying, but it is the world we inhabit. And I can say, from having
	flown in the airspaces of many countries, that there is no other country in
	the world that affords us the same freedom to fly we enjoy – not even Canada,
	although Canada, Australia, and New Zealand are closer than most. (End of
	sermon.)<br>
	<br>
	I like books. Own thousands of them. They are the ultimate random access
	tool. And I always have a copy of the current FAR/AIM, but ASA produces an
	application that I use on my phone and Ipad for ready reference to the FARs. It
	is updated regularly and searchable, which is an asset in my business. You can
	find it in the Apple App store for iPhone.  The same product may well be
	available for Android operating systems.'
	url="https://www.amazon.com/FAR-AIM-2021-Regulations-Aeronautical/dp/1619549506/ref=sr_1_3?dchild=1&keywords=FAR%2FAIM+2021&qid=1611020422&sr=8-3"
/>

<Product title="Weather Theory: Aviation Weather, FAA AC 00-6B"
	pub="ASA reprint (latest edition)"
	notes='This is available for $14.95 with quick delivery from Amazon. This is
	actually an FAA Advisory Circular reprinted by ASA in book form. You can also
	access and/or download it in .pdf form
	<a href="https://www.faa.gov/regulations_policies/advisory_circulars/index.cfm/go/document.information/documentID/1029851">
	from the FAA website</a> for no charge. If you have an earlier edition, it
	will probably suffice. More data and more computing power has vastly improved
	weather forecasting in recent decades but the basic theory – at the level we
	need to know – has been fairly well understood for some years.'
	url="https://www.amazon.com/Aviation-Weather-Advisory-Circular-Handbooks/dp/1619544431/ref=sr_1_1?dchild=1&keywords=Weather+Theory+Weather+Theory%3A+Aviation+Weather%2C+FAA+AC+00-6B&qid=1611016614&sr=8-1"
/>

<Product title="Weather Services: Aviation Weather Services, FAA AC 00-45H"
	pub="ASA reprint (latest edition)"
	notes='This is another FAA advisory circular (AC) reprinted in book form by
	ASA and available for $24.95 w/ quick delivery from Amazon. (Change 1). Also
	available for free <a href="https://www.faa.gov/regulations_policies/advisory_circulars/index.cfm/go/document.list?omni=ACs&rows=10&startAt=0&q=FAA+AC+45H&display=current&parentTopicId"
	from the FAA website</a>.'
	url="https://www.amazon.com/Aviation-Weather-Services-FAA-AC00-45H-Handbooks/dp/1619547023/ref=sr_1_3?dchild=1&keywords=Weather+Services%3A+Aviation+Weather+Services%2C+FAA+AC+45H&qid=1611017055&sr=8-3"
/>

	<div class="subtitle">E6B Flight Computer</div>

	<p>We recommend purchase or acquisition of an E6b.  These are circular slide
	rules for aviation purposes (with, on the reverse side, a grid for calculating
	wind and ground speed corrections ....)  We will spend an entire class learning
	to use these classic aviation devices.  In reality, these days we have software
	that will do these calculations effortlessly, but I believe that these are great
	learning tools to force an understanding of the fundamentals. In addition, they
	are just fascinating devices and a piece of aviation history. Wouldn’t ever want
	to be without one, even if I seldom take it in the airplane anymore and seldom
	use one for serious flight planning. You might enjoy this
	<a href="https://en.wikipedia.org/wiki/E6B">nice Wikipedia article on the history and</a>
	uses of the E6B.

	Here are several available models at different price points.

	<Product title="Flight Computer: Standard full size aluminum E6-B computer, ASA or similar"
	     notes="This is available for $28.50 from Amazon, quick delivery."
	     url="https://www.amazon.com/ASAs-Color-E6B-Flight-Computer/dp/B004X762EY"
	/>

	<p><a href="https://www.amazon.com/Jeppesen-Flight-Computer-Metal-JS514105/dp/B003VSE9K0/ref=sr_1_14?dchild=1&keywords=aviation+Standard+13-inch+Course+Plotter%2C+NPR+NP-13&qid=1611018079&sr=8-14">
	  I actually prefer this Jeppesen model</a>. $42.95 probably available first week of February. Functionally
	  equivalent, but the Jepp model is a little better made and has basic directions printed on it, which are
	  very helpful reminders. However, do you really want to spend 42 bucks for a device that is an
	  anachronism? I would, because I think these devices are fascinating and a piece of aviation history, but
	  that’s just me.
	</p>
	<p>Jepp also makes <a href="https://www.amazon.com/Jeppesen-Student-Flight-Computer-JS514101/dp/B003VSE90A/ref=pd_sbs_2?pd_rd_w=AjThF&pf_rd_p=c52600a3-624a-4791-b4c4-3b112e19fbbc&pf_rd_r=SZ8PXV4Y2XN1HSGMTM10&pd_rd_r=16541551-987d-4e0b-b531-0a20f7feeac0&pd_rd_wg=rYmUb&pd_rd_i=B003VSE90A&psc=1">
	  a “student” model</a>, constructed of plasticized composite (cardboard) material. Not a
	  jewel for the ages, but will get you through the class. $16.94 w/ quick delivery from Amazon.

	<p> And if you have a pilot friend or family member, you might borrow one for
	the duration of the class.  They probably don’t use it very often. </p>

<Product title="The Chart Supplement"
	notes='From the FAA website.  The Chart Supplement, once called the A/FD, will
	be a useful resource.  We suggest you have a copy available, preferably the
	Southeast volume.  If you are using an electronic charting application, such
	as Foreflight, this information will be available to you through the Documents
	setting.  If you want a paper copy, the club has them for sale to members at
	cost along with charts. And you can also find them for access or download
	through the FAA website.'
	url="https://aeronav.faa.gov/afd/31dec2020/SE_rear_31dec2020.pdf"
/>

<Product title="The Aeronautical Chart User’s Guide"

	notes="The July 2020 effective Guide is available at the FAA website.  Earlier
	versions are available at Amazon and elsewhere.  This is the legend for VFR
	and IFR enroute and approach charts, as well as less common planning charts.
	Every symbol is deciphered."
	url="https://www.faa.gov/air_traffic/flight_info/aeronav/digital_products/aero_guide/media/editions/16Jul2020/cug-complete.pdf"
/>





</div>
<style>
a {
  color: blue;
  text-decoration: none;
}
.article {
  margin-left: 3em;
  margin-right: 3em;
}
.article_title {
  font-size: 1.5em;
  border-bottom: 3px solid rgb(40, 90, 149);
}
.subtitle {
  font-weight: bold;
	margin-top: 1.5em;
}
@media (min-width: 480px) {
}
</style>
