<script>
	import { onMount } from 'svelte'
	import { goto } from '$app/navigation'
	import * as notifier from '@beyonk/svelte-notifications/src/notifier.js'
	import { user } from '$lib/store.js'
  import Class from "$lib/components/Class.svelte";
  import Product from "$lib/components/Product.svelte";

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
	<title>WCFC Private Materials</title>
</svelte:head>

<div class="article">
<div class="article_title">WCFC Private Pilot Ground School Class Supplies, January 2021</div>
<div class="author">George Scheer</div>
<div class="label">Chief Flight Instructor</div>
<div class="label">Wings of Carolina</div>

<p>The Syllabus for the Private Pilot Ground School and the many supporting
articles will be distributed to participants via email and will also be posted
and available for quick download from the WCFC Ground School website. </p>

<p>Here is a list of the required books and materials for the Private Pilot
Ground School. The club will have available from our pilot supplies inventory
these books and tools for purchase at our cost. We do not endeavor to make a
profit on these books – they are available for purchase from the club at cost as
a service to our members and ground school participants. We will stock the
current editions. You will want the latest edition of some of these; for others
an older edition will suffice. Some things change; some things seldom do. The
text from Jeppesen should be a current edition; similarly the test prep from
ASA, obviously the FAR/AIM, and the Weather Services. The weather theory doesn’t
have to be the most current. Weather forecasting has improved drastically in
recent years, but our understanding of weather at our level has been well
understood for a few years. See my notes inline. </p>

<p>For those students who find it impossible to visit the club and purchase
directly from the club, I have provided here links to online purchase of these
required materials. The links and prices are current as of January 2021.
Wherever possible, I have included hyperlinks to what I believe would be the
current edition of these publications, usually from Amazon. They are available
elsewhere. Feel free to shop. I just wanted to simplify as much as possible.
</p>

<p>Note: ASA is a publisher of aviation books and material. Along with their original publications, they
reprint FAA documents in book form. Many of these are available for reading or download from the FAA
site for free. You can reference them at
<a href="https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/">this FAA page</a>.
</p>

<p>I have multiple editions of the FAA publications over the years, but I keep a
digital copy downloaded from the FAA website of the current editions on an Ipad
for reference. I use Goodreader as my preferred reader and organization system
for documents. You may prefer something else. </p>

<div class="subtitle">Here are the auxiliary materials for the Private Pilot Ground School Class:</div>

<Product title="Guided Flight Discovery - Private Pilot" publisher="Jeppesen (latest edition)"
      notes='This is available for $79.95 from MyPilotStore This is the
      principal text for the Private Pilot Ground School.  You will want a copy.
      You can also find it from Amazon, but it is difficult to ascertain if you
      are purchasing the most recent edition. The WCFC will have this essential
      text available for purchase.'
			url="https://www.mypilotstore.com/MyPilotStore/sep/1941"
/>

<Product title="Private Pilot Test Prep" publisher="ASA (latest annual edition)"
      notes='This is the study guide for the class. It is available for $16.90
      from Amazon, quick delivery. This is basically a book of questions and
      answers and most of the periodic quizzes we use in the ground school use
      questions gleaned from this book.'
      url="https://www.amazon.com/Private-Pilot-Test-Prep-2021/dp/1619549654/ref=sr_1_2?crid=1T0SFQDZP6HD2&dchild=1&keywords=asa+private+pilot+test+prep+2021&qid=1611016334&sprefix=ASA+Private+Pilot+test+prep%2Caps%2C368&sr=8-2"
/>


<Product title="FAR/AIM" publisher="ASA (latest annual edition)"
      notes="The Code of Federal Regulations is the codification of regulations
      established by the agencies of the Federal Government.  It contains 50
      Titles, one of which, Title 14 Aeronautics and Space, dictates the
      regulatory environment under which we fly. This volume contains those
      Parts of Title 14 that are most relevant to aviation and also the
      Aeronautical Information Manual, which is guidance from the FAA pertinent
      to aviation.  This volume is available in book form for $14.90 as an ASA
      reprint of the FAR/AIM. Available from Amazon w/quick delivery.

      Regulations are not the fun part of flying, but it is the world we
      inhabit. And I can say, from having flown in the airspaces of many
      countries, that there is no other country in the world that affords us the
      same freedom to fly we enjoy – not even Canada, although Canada,
      Australia, and New Zealand are closer than most. (End of sermon.)<br>
			<br>
			I like books. Own thousands of them. They are the ultimate random access
			tool. And I always have a copy of the current FAR/AIM, but for even less money
			ASA produces an application that I use on my phone and Ipad for ready reference
			to the FARs. It is updated regularly and searchable, which is an asset in my
			business. You can find it in the Apple Applications. There are several similar
			products. I can't speak to which is best because I’ve used the ASA product for
			years. I don’t think I’ve ever paid for an update and I’ve used it for years.
			The same product may well be available for Android operating systems."
      url="https://www.amazon.com/FAR-AIM-2021-Regulations-Aeronautical/dp/1619549506/ref=sr_1_3?dchild=1&keywords=FAR%2FAIM+2021&qid=1611020422&sr=8-3"
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

<Product title="Course Plotter: Standard 13-inch Course Plotter, NPR NP-13 or similar"
     notes='This is an ASA product available for $10.58 w/ quick delivery from
     Amazon.. If you have one, no matter how old, it should still serve. This
     basic device has not changed materially in decades.'
     url="https://www.amazon.com/ASA-Fixed-Plotter/dp/B003455WZ6/ref=pd_vtp_2?pd_rd_w=Wg9wY&pf_rd_p=4f2ab3e8-468a-4a7c-9b91-89d6a9221c29&pf_rd_r=D39RMJ22WHBS40ZRPXKA&pd_rd_r=c253e11f-b1b3-40a8-92d5-705a86fbecbf&pd_rd_wg=lM5fe&pd_rd_i=B003455WZ6&psc=1"
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

<div class="subtitle">Here are some important FAA publications that you may want to have.</div>

<Product title="The Airplane Flying Handbook"
  notes='This is available for $14.95 from Amazon w/ quick delivery.
  This is the FAA\’s take on how to fly airplanes. Given that, it\’s pretty decent.
  And, perhaps more to the point, it is the FAA\’s last word on how and how not to
  fly, so for every checkride you ever take, you will know that this is how the
  FAA wants to see it done. You can also find an <a href="https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/airplane_handbook/">
  online version on the FAA website</a>. You can read it online or download it to
  whatever reader you use. I have used Goodreader for quite a while and it does
  fine for me. You may prefer something else.'
  url="https://www.amazon.com/Airplane-Handbook-Federal-Aviation-Administration/dp/1095214446/ref=sr_1_1?crid=105MDFEFGY5TX&dchild=1&keywords=airplane+flying+handbook+2021&qid=1611023689&sprefix=Airplane+Flying+%2Caps%2C159&sr=8-1"
/>


<Product title="Pilot’s Handbook of Aeronautical Knowledge"
  notes='This is available for $16.99 at Amazon, quick delivery. This is the
  technical manual companion to the AFH. A worthwhile reference that covers the
  more technical aspects of flying, such as performance and weight and balance
  calculations and much more. Also <a href="https://www.faa.gov/regulations_policies/handbooks_manuals/aviation/phak/">
  available from the FAA website</a> for reading or download.'
  url="https://www.amazon.com/Pilots-Handbook-Aeronautical-Knowledge-2020/dp/1659619033/ref=sr_1_2?crid=1NKV9UAMPZD4I&dchild=1&keywords=pilot%27s+handbook+of+aeronautical+knowledge+2021&qid=1611024077&sprefix=Pilot%27s+Hand%2Caps%2C164&sr=8-2"
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
