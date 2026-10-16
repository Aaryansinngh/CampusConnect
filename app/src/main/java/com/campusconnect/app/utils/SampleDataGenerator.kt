package com.campusconnect.app.utils

import com.campusconnect.app.data.model.*

object SampleDataGenerator {
    val sampleUsers = mutableListOf(
        User(
            uid = "user_student_1",
            name = "Alex Johnson",
            email = "alex.j@university.edu",
            role = User.ROLE_STUDENT,
            studentId = "2026-CS-104",
            profileImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
        ),
        User(
            uid = "user_club_admin_1",
            name = "Sarah Miller (Coding Club Lead)",
            email = "sarah.m@university.edu",
            role = User.ROLE_CLUB_ADMIN,
            studentId = "2025-CS-012",
            profileImageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150"
        ),
        User(
            uid = "user_super_admin_1",
            name = "Dean Campus Office",
            email = "dean@university.edu",
            role = User.ROLE_SUPER_ADMIN,
            studentId = "FAC-901",
            profileImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150"
        )
    )

    val sampleClubs = mutableListOf(
        Club(
            clubId = "club_coding",
            name = "MUJ Coding Club",
            description = "The premier developer community for competitive programming, web dev, and open source.",
            category = "Technical",
            logoUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=150",
            coverImageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600",
            adminIds = listOf("user_club_admin_1"),
            followerCount = 420,
            approved = true
        ),
        Club(
            clubId = "club_robotics",
            name = "Robotics Club",
            description = "Designing autonomous rovers, drones, and AI-driven hardware systems.",
            category = "Technical",
            logoUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=150",
            coverImageUrl = "https://images.unsplash.com/photo-1561557944-6e7860d1a7eb?w=600",
            adminIds = listOf("user_club_admin_1"),
            followerCount = 310,
            approved = true
        ),
        Club(
            clubId = "club_ecell",
            name = "Entrepreneurship Cell",
            description = "Empowering student founders, startup pitch decks, and angel investor networking.",
            category = "Entrepreneurship",
            logoUrl = "https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=150",
            coverImageUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=600",
            followerCount = 280,
            approved = true
        ),
        Club(
            clubId = "club_photography",
            name = "Photography Club",
            description = "Capturing campus life through lenses. Photo walks, portrait workshops, and exhibitions.",
            category = "Arts",
            logoUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=150",
            coverImageUrl = "https://images.unsplash.com/photo-1452587925148-ce544e77e70d?w=600",
            followerCount = 195,
            approved = true
        ),
        Club(
            clubId = "club_music",
            name = "Music & Jam Club",
            description = "Bands, vocalists, acoustic jam sessions, and annual battle of the bands.",
            category = "Music",
            logoUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150",
            coverImageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600",
            followerCount = 350,
            approved = true
        ),
        Club(
            clubId = "club_dance",
            name = "Dance Society",
            description = "Hip-hop, classical, contemporary, and flash mobs across campus.",
            category = "Dance",
            logoUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=150",
            coverImageUrl = "https://images.unsplash.com/photo-1547153760-18fc86324498?w=600",
            followerCount = 290,
            approved = true
        ),
        Club(
            clubId = "club_sports",
            name = "Campus Sports Club",
            description = "Football, Basketball, Badminton leagues and annual intra-university athletics.",
            category = "Sports",
            logoUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=150",
            coverImageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600",
            followerCount = 510,
            approved = true
        )
    )

    val samplePosts = mutableListOf(
        Post(
            postId = "post_1",
            clubId = "club_coding",
            clubName = "MUJ Coding Club",
            clubLogoUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=150",
            title = "Hackathon 2026 Registrations are NOW OPEN!",
            content = "Gather your teams of 3 to 4 members! 36 hours of non-stop coding, mentorship from industry experts, and prize pools worth $5,000.",
            imageUrl = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=600",
            createdAt = System.currentTimeMillis() - 3600000 * 2,
            likeCount = 84,
            commentCount = 12,
            type = Post.TYPE_ANNOUNCEMENT
        ),
        Post(
            postId = "post_2",
            clubId = "club_robotics",
            clubName = "Robotics Club",
            clubLogoUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=150",
            title = "Robotics Workshop & Demo this Saturday",
            content = "Learn how to build ROS2 navigation pipelines and control quadcopters from scratch. Open to all branches and years!",
            imageUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=600",
            createdAt = System.currentTimeMillis() - 3600000 * 14,
            likeCount = 45,
            commentCount = 5,
            type = Post.TYPE_POST
        ),
        Post(
            postId = "post_3",
            clubId = "club_photography",
            clubName = "Photography Club",
            clubLogoUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=150",
            title = "Golden Hour Photo Walk this Sunday",
            content = "Bring your cameras or smartphones! We are meeting at the Central Amphitheatre at 5:00 PM for a sunset capture session.",
            imageUrl = "https://images.unsplash.com/photo-1452587925148-ce544e77e70d?w=600",
            createdAt = System.currentTimeMillis() - 3600000 * 36,
            likeCount = 62,
            commentCount = 8,
            type = Post.TYPE_POST
        )
    )

    val sampleEvents = mutableListOf(
        Event(
            eventId = "event_1",
            clubId = "club_coding",
            clubName = "MUJ Coding Club",
            title = "Hackathon 2026: Code The Future",
            description = "Join over 300 developers for our biggest annual hackathon. Free meals, swags, and instant hiring opportunities.",
            imageUrl = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=600",
            date = "Oct 24, 2026",
            startTime = "09:00 AM",
            endTime = "09:00 PM",
            location = "Main Auditorium & Innovation Lab",
            registrationDeadline = "Oct 20, 2026",
            capacity = 100,
            registrationCount = 68,
            category = "Technical"
        ),
        Event(
            eventId = "event_2",
            clubId = "club_robotics",
            clubName = "Robotics Club",
            title = "AI & Autonomous Rovers Workshop",
            description = "Hands-on hardware lab session using NVIDIA Jetson and LiDAR sensors.",
            imageUrl = "https://images.unsplash.com/photo-1561557944-6e7860d1a7eb?w=600",
            date = "Nov 02, 2026",
            startTime = "02:00 PM",
            endTime = "06:00 PM",
            location = "Engineering Block 3 - Lab 402",
            registrationDeadline = "Oct 30, 2026",
            capacity = 50,
            registrationCount = 48,
            category = "Technical"
        ),
        Event(
            eventId = "event_3",
            clubId = "club_music",
            clubName = "Music & Jam Club",
            title = "Acoustic Sunset Jam Session",
            description = "An evening of indie tunes, open microphone, and live student performances.",
            imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600",
            date = "Nov 10, 2026",
            startTime = "06:00 PM",
            endTime = "09:00 PM",
            location = "Student Lawn Area",
            registrationDeadline = "Nov 09, 2026",
            capacity = 200,
            registrationCount = 110,
            category = "Music"
        )
    )

    val sampleComments = mutableListOf(
        Comment(
            commentId = "c1",
            postId = "post_1",
            userId = "user_student_1",
            userName = "Alex Johnson",
            userProfileImage = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            text = "Are first-year students allowed to participate?",
            createdAt = System.currentTimeMillis() - 1800000
        ),
        Comment(
            commentId = "c2",
            postId = "post_1",
            userId = "user_club_admin_1",
            userName = "Sarah Miller",
            userProfileImage = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
            text = "Yes! All branches and years are welcome.",
            createdAt = System.currentTimeMillis() - 900000
        )
    )

    val sampleNotifications = mutableListOf(
        NotificationItem(
            notificationId = "n1",
            userId = "user_student_1",
            title = "Registration Confirmed!",
            message = "Your registration for Hackathon 2026 is confirmed.",
            type = NotificationItem.TYPE_REGISTRATION_CONFIRMATION,
            relatedId = "event_1",
            isRead = false
        ),
        NotificationItem(
            notificationId = "n2",
            userId = "user_student_1",
            title = "New Announcement",
            message = "MUJ Coding Club posted a new announcement: Hackathon 2026.",
            type = NotificationItem.TYPE_ANNOUNCEMENT,
            relatedId = "post_1",
            isRead = true
        )
    )
}
