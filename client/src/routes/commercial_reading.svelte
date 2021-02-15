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

<p>The Syllabus for the Commercial Rating Ground School and the many supporting
articles will be distributed to participants via email and will also be posted
and available for quick download from the WCFC Ground School website.</p>

<p>Here is a list of the required books and materials for the Commercial Rating
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


<div class="subtitle">Here are auxiliary materials for the Commercial Ground School Class:</div>

The Commercial Ground School materials still need to be pulled together and published.

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
	sermon.)'
	url="https://www.amazon.com/FAR-AIM-2021-Regulations-Aeronautical/dp/1619549506/ref=sr_1_3?dchild=1&keywords=FAR%2FAIM+2021&qid=1611020422&sr=8-3"
/>

<p>I like books. Own thousands of them. They are the ultimate random access
tool. And I always have a copy of the current FAR/AIM, but ASA produces an
application that I use on my phone and Ipad for ready reference to the FARs. It
is updated regularly and searchable, which is an asset in my business. You can
find it in the Apple App store for iPhone.  The same product may well be
available for Android operating systems.
</p>


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
