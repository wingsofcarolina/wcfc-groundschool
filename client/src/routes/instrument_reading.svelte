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
	<title>WCFC Instrument Materials</title>
</svelte:head>

<div class="article">
<div class="article_title">WCFC Instrument Pilot Ground School Class Supplies, January 2021</div>
<div class="author">George Scheer</div>
<div class="label">Chief Flight Instructor</div>
<div class="label">Wings of Carolina</div>

<p>The Syllabus for the Instrument Rating Ground School and the many supporting
articles will be distributed to participants via email and will also be posted
and available for quick download from the WCFC Ground School website.</p>

<p>Here is a list of the required books and materials for the Instrument Rating
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


<div class="subtitle">Here are auxiliary materials for the Instrument Ground School Class:</div>

<Product title="Jeppesen Guided Flight Discovery Instrument/Commercial (10001784-005) Date: 2016"
	notes='The Jeppesen manual will be our primary text for the Instrument Rating
	Ground School.  You will need a copy of this text.  We will be using the 2016
	edition, the latest edition available as a bound book.   Published:  Available
	for $79.95 from MyPilotStore.<br><br>
	The WCFC has the most recent edition, c. 2016, for sale at cost.'
	url="https://www.mypilotstore.com/MyPilotStore/sep/1270?gclid=CjwKCAiAo5qABhBdEiwAOtGmbtDwkVm1L2liLEhAQ7yH74dhmhfKwVB3pqoFsgAaFQMVhxfkwQNysRoCpkoQAvD_BwE"
/>

<Product title="Jeppesen Guided Flight Discovery Instrument/Commercial (10001784-005) Date: 2016 E-book"
	notes='The Jeppesen website lists only this E-book version as its most current edition.  $73.99 as an E-book.<br><br>
		Amazon lists many volumes of the Instrument / Commercial text, but some are
		obviously out of date and it is difficult to ascertain which edition you are
		purchasing.  And the used inventory constantly changes, so I cannot provide
		a specific link. There are many available used for much less than the new
		copies.  Search for a 2016 edition if you can find it.  I would want a
		volume no older than about 2015 because there have been many changes in
		instrument procedures in recent years.  You might choose an inexpensive used
		copy from Amazon, knowing that it will be out of date.  Most of it will be
		applicable. This is not an FAA publication and so is not available from the
		FAA.'
	url="https://shop.jeppesen.com/All-Products/Training-and-Courseware/Training-Type/E-Books/Instrument-Commercial-E-book/p/10277281"
/>

<Product title="Instrument Flying Handbook (FAA-H-8083-15B) Date: 2012"
	pub="ASA reprint"
	notes='This is available for $23.66 from Amazon, timely delivery. This is an
	FAA manual available for digital download from the FAA website.   It has been
	reprinted in book form by ASA and is available as an ASA book from Amazon. It
	is downloadable in .pdf format <a
	href="https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/instrument_procedures_handbook/media/FAA-H-8083-16B.pdf">
	from the FAA website</a> for free.'
	url="https://www.amazon.com/Instrument-Flying-Handbook-ASA-FAA-H-8083-15B/dp/1619540223"
/>

<Product title="ASA Instrument Procedures Handbook (FAA-H-8083-16B) Date: 2017"
	pub="ASA reprint"
	notes='This is available from Amazon in timely fashion for $29.95.  16B
	appears to be the most current revision.  This is a relatively new FAA
	publication and is one of the better efforts.  You can find it on the
	<a href="https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/instrument_procedures_handbook/media/FAA-H-8083-16B.pdf">
	FAA website</a> in .pdf for viewing or download.'
	url="https://www.amazon.com/Instrument-Procedures-Handbook-FAA-H-8083-16B-Handbooks/dp/1619546337/ref=sr_1_1?crid=66QRCP8OLHFA&dchild=1&keywords=asa+instrument+procedures+handbook&qid=1610957525&sprefix=ASA+Instrument+Procedures+H%2Caps%2C207&sr=8-1"
/>

<Product title="ASA Instrument Rating Test Prep (ASA-TP-I-21) Date: 2021"
	pub="ASA"
	notes='This is an ASA original book available for $19.42 from Amazon in approximately
	2 weeks. The Test Prep is not an FAA document and so is not available on the FAA website.'
	url="https://www.amazon.com/Instrument-Rating-Test-Prep-2021/dp/1619549670/ref=sr_1_3?crid=2INEG77PA5PIX&dchild=1&keywords=asa+instrument+rating+test+prep+2021&qid=1611114437&sprefix=ASA+Instrument+Rating+Test+Prep+%2Caps%2C174&sr=8-3"
/>


<Product title="Weather Theory: Aviation Weather, FAA AC 00-6B" publisher="ASA reprint (latest edition)"
      notes='This is available for $14.95 with quick delivery from Amazon. This
      is actually an FAA Advisory Circular reprinted by ASA in book form. You
      can also access and/or download it in .pdf form
      <a href="https://www.faa.gov/regulations_policies/advisory_circulars/index.cfm/go/document.information/documentID/1029851">
      from the FAA website</a> for no charge.  If you have an earlier edition,
      it will probably suffice.  More data and more computing power has vastly
      improved weather forecasting in recent decades but the basic theory – at
      the level we need to know – has been fairly well understood for some
      years.'
      url="https://www.amazon.com/Aviation-Weather-Advisory-Circular-Handbooks/dp/1619544431/ref=sr_1_1?dchild=1&keywords=Weather+Theory+Weather+Theory%3A+Aviation+Weather%2C+FAA+AC+00-6B&qid=1611016614&sr=8-1"
/>

<Product title="Weather Services: Aviation Weather Services, FAA AC 45H" publisher="ASA reprint (latest edition)"
      notes='This is another FAA advisory circular (AC) reprinted in book form
      by ASA and available for $24.95 w/ quick delivery from Amazon. (Change 1).
      Also available for free
      <a href="https://www.faa.gov/regulations_policies/advisory_circulars/index.cfm/go/document.list?omni=ACs&rows=10&startAt=0&q=FAA+AC+45H&display=current&parentTopicId">
      in digital form from the FAA</a>.'
      url="https://www.amazon.com/Aviation-Weather-Services-FAA-AC00-45H-Handbooks/dp/1619547023/ref=sr_1_3?dchild=1&keywords=Weather+Services%3A+Aviation+Weather+Services%2C+FAA+AC+45H&qid=1611017055&sr=8-3"
/>


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
	I like books. Own thousands of them. They are the ultimate random access
	tool. And I always have a copy of the current FAR/AIM, but ASA produces an
	application that I use on my phone and Ipad for ready reference to the FARs. It
	is updated regularly and searchable, which is an asset in my business. You can
	find it in the Apple App store for iPhone.  The same product may well be
	available for Android operating systems.'
	url="https://www.amazon.com/FAR-AIM-2021-Regulations-Aeronautical/dp/1619549506/ref=sr_1_3?dchild=1&keywords=FAR%2FAIM+2021&qid=1611020422&sr=8-3"
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
